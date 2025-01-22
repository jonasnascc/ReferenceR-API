package be.wanna.Referencerback.service.collections;

import be.wanna.Referencerback.dto.album.AlbumDTO;
import be.wanna.Referencerback.dto.userCollection.*;
import be.wanna.Referencerback.dto.photo.PhotoDTO;
import be.wanna.Referencerback.entity.album.Album;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.entity.album.AlbumPhotosByPage;
import be.wanna.Referencerback.entity.collections.CollectionLog;
import be.wanna.Referencerback.entity.collections.UserCollection;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.repository.*;
import be.wanna.Referencerback.service.album.AlbumsService;
import be.wanna.Referencerback.service.photo.PhotoService;
import be.wanna.Referencerback.service.scraping.DeviantArtService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
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

    private final PhotoAlbumPageRepository photoAlbumPageRepository;

    private final DeviantArtService deviantArtService;

    private final ModelMapper modelMapper;


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
                .map(col -> modelMapper.map(col, CollectionDTOOut.class)).collect(Collectors.toList());
    }

    public PhotoDTO getCollectionThumbnail(String login, Long id) {
        User user = checkUser(login);
        UserCollection col = repository.findByUserAndId(user, id);

        if(col == null) throw new RuntimeException("Collection not found.");

        return getCollectionThumbnail(col);
    }

    private PhotoDTO getCollectionThumbnail(UserCollection col) {
        PhotoDTO thumbnail = null;

        Set<CollectionLog> logs = col.getLogs();

        if(!logs.isEmpty()) {
            CollectionLog lastLog = null;

            for(CollectionLog log : logs) {
                if(lastLog == null) lastLog = log;
                else if(log.getDate().after(lastLog.getDate())) lastLog = log;
            }

            Set<Photo> logPhotos = lastLog.getPhotos();
            if(!logPhotos.isEmpty()) {
                Photo lastPhoto = null;
                for(Photo photo : logPhotos) {
                    if(lastPhoto == null) lastPhoto = photo;
                    else if(photo.getPublishedTime().after(lastPhoto.getPublishedTime())) lastPhoto = photo;
                }

                try{
                    thumbnail = dvArtService.getDeviationWithToken(lastPhoto, lastPhoto.getAuthor().getName());
                }catch (Exception e) {
                    thumbnail = modelMapper.map(lastPhoto, PhotoDTO.class);
                }
            }
        }
        return thumbnail;
    }

    public List<AlbumDTO> listAsAlbums(String login) {
        User user = checkUser(login);
        List<UserCollection> list = repository.findByUser(user);

        return list.stream().map(col -> {
            PhotoDTO thumbnail = getCollectionThumbnail(col);

            return new AlbumDTO(
                    col.getId(),
                    "collection-".concat(col.getId().toString()).concat(":").concat(user.getId()),
                    col.getName(),
                    "",
                    thumbnail,
                    user.getId(),
                    "",
                    col.getPhotos().size()
            );
        }).collect(Collectors.toList());

    }

    public CollectionDTOOut find(String login, Long id){
        User user = checkUser(login);
        UserCollection col = repository.findByUserAndId(user, id);

        if(col == null) throw new RuntimeException("Collection not found.");

        return modelMapper.map(col, CollectionDTOOut.class);
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
                if(alb.photos() != null) {
                    alb.photos().forEach(ph -> addPhotoFromAlbum(alb, ph, collection, log));
                }
            });
        }
    }

    @Transactional
    protected void addPhotoFromAlbum(AlbumCollectionDTOIn alb, PhotoDTO photoDto, UserCollection collection, CollectionLog log) {
        Photo photo = convertPhotoDto(photoDto);

        AlbumDTO albDto = alb.album();

        Album album = convertAlbumDtoPersist(albDto);

        AlbumPhotosByPage phAlbumPage = photoAlbumPageRepository.findByPageAndAlbumId(null, album.getId())
                .orElseGet(() -> photoAlbumPageRepository.save(new AlbumPhotosByPage(
                        null,
                        album
                )));

        photo.addPhotoAlbumPage(phAlbumPage);
        photo.setAuthor(album.getAuthor());

        photo.setSavedDate(new Date());
        Photo savedPhoto = photoRepository.save(photo);

        phAlbumPage.addPhoto(savedPhoto);
        photoAlbumPageRepository.save(phAlbumPage);

        album.addPhoto(savedPhoto);
        savedPhoto.addCollection(collection);

        if(collection.getPhotos().stream().noneMatch(ph -> ph.getCode().equals(savedPhoto.getCode()))){
            collection.addPhoto(savedPhoto);
            log.addPhoto(savedPhoto);
        }
    }

    private Album convertAlbumDtoPersist(AlbumDTO albDto) {
        return albumRepository.findByCodeAndProvider(
                albDto.code(), albDto.provider()
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

        Set<Album> albums = photo.getAlbums();
        if(photo.getCollections().size() <= 1) {
            photoRepository.delete(photo);
        }
        albums.forEach(alb -> {
            if(alb.getPhotos().isEmpty()) albumRepository.delete(alb);
        });
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

    public List<PhotoDTO> listAlbumPhotos(String login, Long collectionId, Long albumId) {
        User user = checkUser(login);

        UserCollection collection = repository.findByUserAndId(user, collectionId);
        if(collection==null) throw new RuntimeException("Collection not found.");

        Optional<Album> optionalAlbum = albumRepository.findById(albumId);
        if(optionalAlbum.isEmpty()) throw new RuntimeException("Album not found.");

        Album album = optionalAlbum.get();
        List<AlbumPhotosByPage> albumPages = photoAlbumPageRepository.findPhotoAlbumPageByAlbum_Id(album.getId());

        List<Photo> photos = new ArrayList<>();

        albumPages.forEach(albumPage -> photos.addAll(
                albumPage.getPhotos().stream()
                            .filter(ph -> collection.getPhotos().stream().anyMatch(photo -> photo.getId().equals(ph.getId())))
                            .collect(Collectors.toSet())
            )
        );

        return photos.stream().map(ph -> modelMapper.map(ph, PhotoDTO.class)).collect(Collectors.toList());
    }

    public Set<PhotoDTO> listPhotos(String login, Long id, Integer page, Integer limit) {
        User user = checkUser(login);

        UserCollection collection = repository.findByUserAndId(user, id);
        if(collection==null) throw new RuntimeException("Collection not found.");

        if(page==null) return listPhotos(collection);

        PageRequest pageable = PageRequest.of(page - 1, limit);
        Page<Photo> photos = repository.listPhotosByCollectionId_OrderBySavedDateDesc(id, pageable);

        System.out.println(photos.getTotalElements());
        System.out.println(photos.getTotalPages());
        System.out.println(page);
        System.out.println(limit);

        return photos.get().map(this::getPhotoDtoWithUpdatedToken).collect(Collectors.toSet());
    }

    private Set<PhotoDTO> listPhotos(UserCollection collection) {
        return collection.getPhotos().stream().map(this::getPhotoDtoWithUpdatedToken).collect(Collectors.toSet());
    }

    private PhotoDTO getPhotoDtoWithUpdatedToken(Photo ph) {
        PhotoDTO dto = modelMapper.map(ph, PhotoDTO.class);
        String token = dto.getToken();
        if(token==null || dto.getTokenExpireTime().before(new Date())) {
            token = deviantArtService.getDeviationToken(ph);
            if(token!=null) {
                ph.setToken(token);
                ph.setTokenExpireTime(Date.from(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(20).atZone(ZoneId.of("America/Sao_Paulo")).toInstant()));
                photoRepository.save(ph);
            }
        }
        dto.setUrl(ph.getUrl() + (ph.getToken()!= null ? "?token=" + ph.getToken() : ""));
        return dto;
    }

    private Photo convertPhotoDto(PhotoDTO dto) {
        Optional<Photo> saved = photoRepository.findPhotoByCode(dto.getCode());

        String url = formatUrl(dto.getUrl());
        String thumbUrl = formatUrl(dto.getThumbUrl());
        return saved.orElseGet(() -> {
            Photo photo = modelMapper.map(dto, Photo.class);
            photo.setUrl(url);
            photo.setThumbUrl(thumbUrl);
            return photo;
        });
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
