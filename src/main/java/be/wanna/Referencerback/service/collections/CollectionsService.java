package be.wanna.Referencerback.service.collections;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.dto.userCollection.*;
import be.wanna.Referencerback.dto.PhotoDTO;
import be.wanna.Referencerback.entity.Album;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.entity.collections.CollectionLog;
import be.wanna.Referencerback.entity.collections.UserCollection;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.repository.*;
import be.wanna.Referencerback.service.album.AlbumsService;
import be.wanna.Referencerback.service.scraping.DeviantArtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CollectionsService {
    private final CollectionRepository repository;
    private final UserRepository userRepository;

    private final PhotoRepository photoRepository;

    private final AlbumRepository albumRepository;

    private final ProviderRepository providerRepository;

    private final DeviantArtService dvArtService;

    private final AuthorRepository authorRepository;

    private final CollectionLogRepository collectionLogRepository;

    public Long create(String login, CollectionDTOIn dto){
        User user = checkUser(login);

        UserCollection collection = new UserCollection(
                dto.name(),
                dto.description()
        );

        user.addCollection(collection);

        return repository.save(collection).getId();
    }

    public List<CollectionDTOOut> list(String login) {
        User user = checkUser(login);
        return repository.findByUser(user).stream()
                .map(CollectionsService::convertCollection).collect(Collectors.toList());
    }

    public CollectionDTOOut find(String login, Long id){
        User user = checkUser(login);
        UserCollection col = repository.findByUserAndId(user, id);

        if(col == null) throw new RuntimeException("Collection not found.");

        return convertCollection(col);
    }

    @Transactional
    public Long update(String login, Long id, CollectionDTOIn dto) {
        return null;
    }

    public void delete(String login, Long id){

    }

    public Long addPhotos(String login, Long id, CollectionPhotosDTO dto){
        User user = checkUser(login);
        UserCollection collection = repository.findByUserAndId(user, id);
        if(collection == null) throw  new RuntimeException("Collection not found.");

        CollectionLog log = new CollectionLog();

        addAlbumPhotos(dto, collection, log);

        if((log.getPhotos() != null&&!log.getPhotos().isEmpty()) || (log.getAlbums()!=null&&!log.getAlbums().isEmpty())){
            log.setDate(new Date());
            CollectionLog savedLog = collectionLogRepository.save(log);
            savedLog.setCollection(collection);

            collection.addLog(savedLog);
        }


        return repository.save(collection).getId();
    }


    private void addAlbumPhotos(CollectionPhotosDTO dto, UserCollection collection, CollectionLog log) {
        if(!dto.albums().isEmpty()) {
            dto.albums().forEach(alb -> {
                AlbumDTO albumDTO = alb.album();

                if(alb.photos() != null) {
                    alb.photos().forEach(ph -> addPhotoFromAlbum(alb, ph, albumDTO, collection, log));
                }
            });
        }
    }

    @Transactional
    protected void addPhotoFromAlbum(AlbumCollectionDTOIn alb, PhotoDTO photoDto, AlbumDTO albumDTO, UserCollection collection, CollectionLog log) {
        Photo photo = convertPhotoDto(photoDto);

        AlbumDTO albDto = alb.album();

        Album album = albumRepository.findByCodeAndProvider(
                albumDTO.code(), albumDTO.provider()
        ).orElseGet(() -> albumRepository.save(new Album(
                albDto.code(),
                albDto.name(),
                albDto.url(),
                albDto.size(),
                authorRepository.findAuthorByNameAndProvider(albDto.author(), albDto.provider())
                                .orElseGet(() -> authorRepository.save(new Author(
                                        albDto.author(),
                                        "",
                                        providerRepository.findById(albDto.provider())
                                                .orElseGet(() -> providerRepository.save(new Provider(albDto.provider())))
                                ))),
                providerRepository.findById(albDto.provider())
                        .orElseGet(() -> providerRepository.save(new Provider(albDto.provider())))
        )));

        photo.addAlbum(album);

        Photo savedPhoto = photoRepository.save(photo);

        album.addPhoto(savedPhoto);
        savedPhoto.addCollection(collection);

        if(collection.getPhotos().stream().noneMatch(ph -> ph.getCode().equals(savedPhoto.getCode()))){
            collection.addPhoto(savedPhoto);
            log.addPhoto(savedPhoto);
        }
    }

    public Set<AlbumDTO> listAlbums(String login, Long id) {
        User user = checkUser(login);

        UserCollection collection = repository.findByUserAndId(user, id);
        if(collection==null) throw new RuntimeException("Collection not found.");

        Map<String, Album> albumsMap = new HashMap<>();

        collection.getPhotos().forEach(ph -> {
            ph.getAlbums().forEach(alb -> {
                if(!albumsMap.containsKey(alb.getCode())) {
                    albumsMap.put(alb.getCode(), alb);
                }
            });
        });

        return albumsMap.values().stream().map(AlbumsService::convertDTO).collect(Collectors.toSet());
    }

    public List<Photo> listAlbumPhotos(String login, Long collectionId, Long albumId) {
        User user = checkUser(login);

        UserCollection collection = repository.findByUserAndId(user, collectionId);
        if(collection==null) throw new RuntimeException("Collection not found.");

        Optional<Album> optionalAlbum = albumRepository.findById(albumId);
        if(optionalAlbum.isEmpty()) throw new RuntimeException("Album not found.");

        Album album = optionalAlbum.get();
        Set<Photo> photos = collection.getPhotos().stream()
                .filter(ph -> album.getPhotos().stream().anyMatch(albph -> albph.getCode().equals(ph.getCode())))
                .collect(Collectors.toSet());

        Map<Integer, Set<Photo>> photosByPages = new HashMap<>();

        photos.forEach(ph -> {
            if(!photosByPages.containsKey(ph.getPage())){
                photosByPages.put(ph.getPage(), new HashSet<>());
            }

            photosByPages.get(ph.getPage()).add(ph);
        });

        List<Photo> photosArray = new ArrayList<>();

        photosByPages.keySet().forEach(page -> {
            Set<Photo> toAdd = dvArtService.listAlbumPhotosByPage(
                    album.getCode(),
                    album.getAuthor().getName(),
                    page,
                    50,
                    500
            );

            Set<String> pagePhotos = new HashSet<>();

            photosByPages.get(page).forEach(photo -> pagePhotos.add(photo.getCode()));

            photosArray.addAll(toAdd.stream().filter(photo -> pagePhotos.contains(photo.getCode())).collect(Collectors.toSet()));
        });

        return photosArray;
    }

    public Set<Photo> listPhotos(String login, Long id) {
        User user = checkUser(login);

        UserCollection collection = repository.findByUserAndId(user, id);
        if(collection==null) throw new RuntimeException("Collection not found.");

        Set<Photo> collectionPhotos = collection.getPhotos();

        Map<String, Album> albumsMap = new HashMap<>();

        Map<String, Set<Integer>> albumCodeByPages = new HashMap<>();

        Map<String, List<Photo>> albumCodeByPhotoMap = new HashMap<>();
        collectionPhotos.forEach(ph -> {
            ph.getAlbums().forEach(alb -> {
                if(!albumsMap.containsKey(alb.getCode())) {
                    albumsMap.put(alb.getCode(), alb);
                }
                if(!albumCodeByPhotoMap.containsKey(alb.getCode())){
                    albumCodeByPhotoMap.put(alb.getCode(), new ArrayList<>());
                }
                albumCodeByPhotoMap.get(alb.getCode()).add(ph);
                if(!albumCodeByPages.containsKey(alb.getCode())){
                    albumCodeByPages.put(alb.getCode(), new HashSet<>());
                }
                albumCodeByPages.get(alb.getCode()).add(ph.getPage());
            });
        });

        Set<Photo> photos = new HashSet<>();
        albumsMap.keySet().forEach(albCode -> {
            Album album = albumsMap.get(albCode);
            Set<Integer> pages = albumCodeByPages.get(albCode);

            List<Photo> photosArray = new ArrayList<>();

            pages.forEach(page -> {
                photosArray.addAll(dvArtService.listAlbumPhotosByPage(
                        album.getCode(),
                        album.getAuthor().getName(),
                        page,
                        50,
                        500
                ));
            });


            photosArray.forEach(ph -> {
                if(albumCodeByPhotoMap.get(album.getCode()).stream().anyMatch(p -> p.getCode().equals(ph.getCode()))){
                    if(photos.stream().noneMatch(p -> p.getCode().equals(ph.getCode()))){
                        photos.add(ph);
                    }
                }
            });
        });

        return photos;
    }
//    public Set<Photo> listPhotos(String login, Long id) {
//        User user = checkUser(login);
//
//        UserCollection collection = repository.findByUserAndId(user, id);
//        if (collection == null) throw new RuntimeException("Collection not found.");
//
//        Set<Photo> photos = new HashSet<>();
//        collection.getPhotos().forEach(photo -> {
//           Album album = new ArrayList<>(photo.getAlbums()).get(0);
//           if(album!=null) {
//               photos.add(dvArtService.getSingleDeviation(photo.getCode(), album.getAuthor().getName()));
//           }
//        });
//
//        return photos;
//    }

    public static CollectionDTOOut convertCollection(UserCollection collection) {
        return new CollectionDTOOut(
                collection.getId(),
                collection.getName(),
                collection.getDescription()
        );
    }


    private Photo convertPhotoDto(PhotoDTO dto) {
        Optional<Photo> saved = photoRepository.findPhotoByCode(dto.code());

        String url = formatUrl(dto.url());
        String thumbUrl = formatUrl(dto.thumbUrl());
        return saved.orElseGet(() -> new Photo(
                dto.code(),
                dto.title(),
                url,
                dto.mature(),
                dto.type(),
                thumbUrl,
                dto.matureLevel(),
                dto.photoPage(),
                dto.license(),
                dto.publishedTime(),
                dto.page()
        ));
    }

    private static String formatUrl(String text) {
        int tokenIndex = text.indexOf("?token");

        String url = text;
        if(tokenIndex!=-1) url = url.substring(0, tokenIndex);

        if(url.length() > 255) url = url.substring(0, 255);
        return url;
    }


    private User checkUser(String login){
        User user =  userRepository.findByLogin(login);
        if(user==null) throw new RuntimeException("User not found.");
        return user;
    }
}
