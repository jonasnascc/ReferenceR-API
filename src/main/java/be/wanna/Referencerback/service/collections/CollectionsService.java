package be.wanna.Referencerback.service.collections;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.dto.userCollection.*;
import be.wanna.Referencerback.dto.PhotoDTO;
import be.wanna.Referencerback.entity.Album;
import be.wanna.Referencerback.entity.UserCollection;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.repository.AlbumRepository;
import be.wanna.Referencerback.repository.CollectionRepository;
import be.wanna.Referencerback.repository.PhotoRepository;
import be.wanna.Referencerback.repository.UserRepository;
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

    private final DeviantArtService dvArtService;

    @Transactional
    public Long create(String login, CollectionDTOIn dto){
//        if(dto.photos().isEmpty()) throw new RuntimeException("Photos must not be empty or null.");
        User user = checkUser(login);


        UserCollection collection = new UserCollection(
                dto.name(),
                dto.description()
        );

        UserCollection col = repository.save(collection);

        user.addCollection(col);
        User savedUser = userRepository.save(user);

        collection.setUser(savedUser);

        return repository.save(col).getId();
    }

    public List<CollectionDTOOut> list(String login) {
        User user = checkUser(login);
        return repository.findByUser(user).stream()
                .map(this::convertCollection).collect(Collectors.toList());
    }

    public CollectionDTOOut find(String login, Long id){
        User user = checkUser(login);
        UserCollection col = repository.findByUserAndId(user, id);

        if(col == null) throw new RuntimeException("Collection not found.");

        return  convertCollection(col);
    }

    @Transactional
    public Long update(String login, Long id, CollectionDTOIn dto) {
        User user = checkUser(login);
        UserCollection saved =  repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found."));

        UserCollection collection = new UserCollection(
                dto.name(),
                dto.description()
        );

        if(!dto.albums().isEmpty()) {
            dto.albums().forEach(alb -> {
                if(!alb.photos().isEmpty()) {
                    Optional<Album> optAlbum = albumRepository.findByCodeAndAuthorAndProvider(
                            alb.album().code(),
                            alb.album().author(),
                            alb.album().provider()
                    );

                    if(optAlbum.isEmpty()) throw new RuntimeException("Album not found in database: " + alb.album().code());

                    Album album = optAlbum.get();

                    Set<Photo> photos = alb.photos().stream()
                            .map(ph -> {
                                Photo photo = convertPhotoDtoPersist(convertCollectionPhotoToPhotoDto(ph));
                                photo.addAlbum(album);
                                return photoRepository.save(photo);
                            })
                            .collect(Collectors.toSet());

                    photos.forEach(album::addPhoto);

                    albumRepository.save(album);
                }
            });
        }
        return repository.save(collection).getId();
    }

    public void delete(String login, Long id){
        User user = checkUser(login);
        UserCollection col = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found."));

        repository.delete(col);
    }

    @Transactional
    public void addPhotos(String login, Long id, CollectionPhotosDTO dto){
        User user = checkUser(login);
        UserCollection collection = repository.findByUserAndId(user, id);
        if(collection == null) throw  new RuntimeException("Collection not found.");

        if(!dto.albums().isEmpty()) {
            dto.albums().forEach(alb -> {
                AlbumDTO albumDTO = alb.album();

                if(alb.photos() != null)
                    alb.photos().forEach(ph -> {
                        Photo photo = convertPhotoDtoPersist(convertCollectionPhotoToPhotoDto(ph));

                        Optional<Album> optAlbum = albumRepository.findByCodeAndAuthorAndProvider(
                                albumDTO.code(),
                                albumDTO.author(),
                                albumDTO.provider()
                        );

                        AlbumDTO albDto = alb.album();
                        Album album;
                        if(optAlbum.isPresent()) album = optAlbum.get();
                        else album = albumRepository.save(new Album(
                                albDto.code(),
                                albDto.name(),
                                albDto.url(),
                                albDto.size()
                        ));

                        album.addPhoto(photo);

                        Album svdAlbum = albumRepository.save(album);
                        photo.addAlbum(svdAlbum);

                        Photo svdPhoto = photoRepository.save(photo);

                        collection.addPhoto(svdPhoto);
                    });
            });

        }


        UserCollection saved = repository.save(collection);

        collection.getPhotos().forEach(ph -> {
            ph.addCollection(saved);
        });

        photoRepository.saveAll(collection.getPhotos());
    }

    public List<PhotoDTO> listPhotos(String login, Long id) {
        User user = checkUser(login);
        UserCollection collection = repository.findByUserAndId(user, id);
        if(collection==null) throw new RuntimeException("Collection not found.");

        Set<Photo> colPhotos = collection.getPhotos();
        if(colPhotos.isEmpty()) return Collections.emptyList();

        Map<Integer, List<String>> pageByCodeMap = new HashMap<>();

        colPhotos.forEach(ph -> {
            if(ph.getPage() != null){
                Integer page = ph.getPage();
                if(!pageByCodeMap.containsKey(page)) {
                    pageByCodeMap.put(page, new ArrayList<>());
                }
                else {
                    List<String> codes = pageByCodeMap.get(page);

                    if(!codes.contains(ph.getCode()))
                        codes.add(ph.getCode());
                }
            }
        });

        List<PhotoDTO> photos = new ArrayList<>();

        Set<Integer> mapKeys = pageByCodeMap.keySet();

//        mapKeys.forEach(key -> {
//            dvArtService.listAlbumPhotosByPage();
//        });

        return photos;
    }

    private CollectionDTOOut convertCollection(UserCollection collection) {
        return new CollectionDTOOut(
                collection.getId(),
                collection.getName(),
                collection.getDescription()
        );
    }

    private PhotoDTO convertCollectionPhotoToPhotoDto(UserCollectionPhotoDTO dto) {
        return new PhotoDTO(
                dto.code(),
                dto.page()
        );
    }

    @Transactional
    protected Photo convertPhotoDtoPersist(PhotoDTO photo) {
        return convertPhotoDto(photo, true);
    }
    private Photo convertPhotoDto(PhotoDTO photo) {
        return convertPhotoDto(photo, false);
    }

    private Photo convertPhotoDto(PhotoDTO dto, boolean persist) {
        Photo photo = new Photo(
                dto.code(),
                dto.title(),
                dto.url(),
                dto.mature(),
                dto.page()
        );

        return persist ? persistPhoto(photo) : photo ;
    }

    private Photo persistPhoto(Photo photo) {
        Optional<Photo> opt = photoRepository.findPhotoByCode(photo.getCode());

        if(opt.isEmpty()) return photoRepository.save(photo);

        Photo ph = opt.get();

        if(ph.getPage()==null && photo.getPage()!=null) {
            ph.setPage(photo.getPage());
            return photoRepository.save(ph);
        };

        return ph;
    }

    private User checkUser(String login){
        User user =  userRepository.findByLogin(login);
        if(user==null) throw new RuntimeException("User not found.");
        return user;
    }
}
