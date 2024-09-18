package be.wanna.Referencerback.service.collections;

import be.wanna.Referencerback.dto.album.ScrapAlbumDTO;
import be.wanna.Referencerback.dto.album.collection.UserCollectionDTO;
import be.wanna.Referencerback.dto.userCollection.*;
import be.wanna.Referencerback.dto.PhotoDTO;
import be.wanna.Referencerback.entity.album.Album;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.entity.album.ScrapAlbum;
import be.wanna.Referencerback.entity.collections.CollectionLog;
import be.wanna.Referencerback.entity.album.UserCollection;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.repository.*;
import be.wanna.Referencerback.service.album.ScrapAlbumService;
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

    private final ScrapAlbumRepository scrapAlbumRepository;

    private final ProviderRepository providerRepository;

    private final DeviantArtService dvArtService;

    private final AuthorRepository authorRepository;

    private final CollectionLogRepository collectionLogRepository;

    private final ScrapAlbumService scrapAlbumService;

    public Long create(String login, CollectionDTOIn dto){
        User user = checkUser(login);

        UserCollection collection = new UserCollection(
                dto.name(),
                dto.description(),
                user
        );

        user.addCollection(collection);

        return repository.save(collection).getId();
    }

    public List<UserCollectionDTO> list(String login) {
        User user = checkUser(login);
        return repository.findByUser(user).stream()
                .map(this::convertCollection).collect(Collectors.toList());
    }

    public UserCollectionDTO find(String login, Long id){
        User user = checkUser(login);
        UserCollection col = repository.findByUserAndId(user, id);

        if(col == null) throw new RuntimeException("Collection not found.");

        return convertCollection(col);
    }

    @Transactional
    public Long update(String login, Long id, CollectionDTOIn dto) {
        return null;
    }

    @Transactional
    public void delete(String login, Long id){
        User user = checkUser(login);
        UserCollection collection = repository.findByUserAndId(user, id);
        if(collection==null) throw new RuntimeException("Collection not found.");

        deletePhotos(login,
                collection.getId(),
                collection.getPhotos().stream().map(ph -> ph.getId())
                        .collect(Collectors.toList()));

        repository.delete(collection);
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



    @Transactional
    protected void addAlbumPhotos(CollectionPhotosDTO dto, UserCollection collection, CollectionLog log) {
        if(!dto.albums().isEmpty()) {
            dto.albums().forEach(alb -> {
                ScrapAlbumDTO scrapAlbumDTO = alb.album();

                if(alb.photos() != null) {
                    alb.photos().forEach(ph -> addPhotoFromAlbum(alb, ph, scrapAlbumDTO, collection, log));
                }
            });
        }
    }

    protected void addPhotoFromAlbum(AlbumCollectionDTOIn alb, PhotoDTO photoDto, ScrapAlbumDTO scrapAlbumDTO, UserCollection collection, CollectionLog log) {
        Photo photo = convertPhotoDto(photoDto);

        ScrapAlbumDTO albDto = alb.album();

        ScrapAlbum album = scrapAlbumRepository.findByCodeAndProvider(
                scrapAlbumDTO.code(), scrapAlbumDTO.provider()
        ).orElseGet(() -> getScrapAlbum(albDto));

        photo.addAlbum(album);
        photo.addCollection(collection);
        photo.setAuthor(album.getAuthor());
        Photo savedPhoto = photoRepository.save(photo);

        album.addPhoto(savedPhoto);
        collection.addPhoto(savedPhoto);
        if(collection.getPhotos()!=null && collection.getPhotos().stream().noneMatch(ph -> ph.getCode().equals(savedPhoto.getCode()))){

            log.addPhoto(savedPhoto);
        }
    }

    private ScrapAlbum getScrapAlbum(ScrapAlbumDTO albDto) {
        Provider provider = providerRepository.findById(albDto.provider())
            .orElseGet(() -> providerRepository.save(new Provider(albDto.provider())));

        Author author = authorRepository.findAuthorByNameAndProvider(albDto.author(), albDto.provider())
            .orElseGet(() -> authorRepository.save(new Author(
                    albDto.author(),
                    "",
                    provider
            ))
        );

        return scrapAlbumRepository.save(new ScrapAlbum(
                albDto.code(),
                albDto.name(),
                albDto.url(),
                albDto.size(),
                provider,
                author
        ));
    }

    @Transactional
    public void deletePhoto(String login, Long collectionId, Long photoId) {
        User user = checkUser(login);
        deleteCollectionPhoto(collectionId, photoId, user);
    }

    @Transactional
    public void deletePhotos(String login, Long collectionId, List<Long> photoIds) {
        User user = checkUser(login);

        photoIds.forEach(id -> {
            deleteCollectionPhoto(collectionId, id, user);
        });
    }

    private void deleteCollectionPhoto(Long collectionId, Long photoId, User user) {
        UserCollection collection = repository.findByUserAndId(user, collectionId);
        if(collection==null) throw new RuntimeException("Collection not found.");

        Photo photo = collection.getPhotos().stream().filter(ph -> ph.getId().equals(photoId)).findAny()
                .orElseThrow(() -> new RuntimeException("This photo does not exists on this Collection."));

        Set<ScrapAlbum> albums = photo.getScrapAlbums();
        if(photo.getCollections().size() <= 1) {
            photoRepository.delete(photo);
        }
        albums.forEach(alb -> {
            if(alb.getPhotos().isEmpty()) scrapAlbumRepository.delete(alb);
        });
    }

    public Set<ScrapAlbumDTO> listAlbums(String login, Long id) {
        User user = checkUser(login);

        UserCollection collection = repository.findByUserAndId(user, id);
        if(collection==null) throw new RuntimeException("Collection not found.");

        Map<String, ScrapAlbum> albumsMap = new HashMap<>();

        if(collection.getPhotos() == null) return Collections.emptySet();


        collection.getPhotos().forEach(ph -> {
            ph.getScrapAlbums().forEach(alb -> {
                if(!albumsMap.containsKey(alb.getCode())) {
                    albumsMap.put(alb.getCode(), alb);
                }
            });
        });

        return albumsMap.values().stream()
                .map(ScrapAlbumService::convertDTO)
                .collect(Collectors.toSet());
    }
    public List<Photo> listAlbumPhotos(String login, Long collectionId, Long albumId) {
        User user = checkUser(login);

        UserCollection collection = repository.findByUserAndId(user, collectionId);
        if(collection==null) throw new RuntimeException("Collection not found.");

        Optional<ScrapAlbum> optionalAlbum = scrapAlbumRepository.findById(albumId);
        if(optionalAlbum.isEmpty()) throw new RuntimeException("Album not found.");

        ScrapAlbum album = optionalAlbum.get();
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

            toAdd.forEach(ph -> {
                Photo saved = photosByPages.get(page).stream()
                        .filter(photo -> photo.getCode().equals(ph.getCode()))
                        .findAny()
                        .orElse(null);

                if(saved!=null){
                    ph.setId(saved.getId());
                    photosArray.add(ph);
                }
            });

            Set<String> pagePhotos = new HashSet<>();

            photosByPages.get(page).forEach(photo -> pagePhotos.add(photo.getCode()));
        });

        return photosArray;
    }

    public Set<Photo> listPhotos(String login, Long id) {
        User user = checkUser(login);

        UserCollection collection = repository.findByUserAndId(user, id);
        if(collection==null) throw new RuntimeException("Collection not found.");

        Set<Photo> collectionPhotos = collection.getPhotos();

        Map<String, ScrapAlbum> albumsMap = new HashMap<>();

        Map<String, Set<Integer>> albumCodeByPages = new HashMap<>();

        Map<String, List<Photo>> albumCodeByPhotoMap = new HashMap<>();

        collectionPhotos.forEach(ph -> {
            ph.getScrapAlbums().forEach(alb -> {
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
            ScrapAlbum album = albumsMap.get(albCode);
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

    public UserCollectionDTO convertCollection(UserCollection collection) {
        Set<Photo> photos = collection.getPhotos();

        return new UserCollectionDTO(
                collection.getId(),
                collection.getName(),
                collection.getDescription(),
                photos==null ? 0 : photos.size()
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

    public List<ScrapAlbumDTO> listAsAlbums(String login) {
        User user = checkUser(login);
        List<UserCollection> list = repository.findByUser(user);

        return list.stream().map(col -> {
            Set<Photo> photos = col.getPhotos();

            PhotoDTO thumbnail = null;
            if(!photos.isEmpty()) {
                Photo photo = new ArrayList<>(photos).get(0);

                try{
                    thumbnail = dvArtService.getDeviationWithToken(photo, photo.getAuthor().getName());
                }catch (Exception e) {
                    thumbnail = new PhotoDTO(
                            photo.getId(),
                            photo.getCode(),
                            "",
                            photo.getUrl(),
                            photo.getTitle(),
                            photo.getMature()
                    );
                }
            }


            return new ScrapAlbumDTO(
                    col.getId(),
                    "collection-".concat(col.getId().toString()).concat(":").concat(user.getId()),
                    col.getName(),
                    "",
                    thumbnail,
                    user.getId(),
                    "",
                    photos.size()
            );
        }).collect(Collectors.toList());

    }
}
