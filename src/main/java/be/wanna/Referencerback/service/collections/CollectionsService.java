package be.wanna.Referencerback.service.collections;

import be.wanna.Referencerback.dto.userCollection.AlbumCollectionDTO;
import be.wanna.Referencerback.dto.userCollection.CollectionDTOIn;
import be.wanna.Referencerback.dto.PhotoDTO;
import be.wanna.Referencerback.dto.userCollection.CollectionDTOOut;
import be.wanna.Referencerback.dto.userCollection.PhotoByPageDTO;
import be.wanna.Referencerback.entity.UserCollection;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.repository.CollectionRepository;
import be.wanna.Referencerback.repository.PhotoRepository;
import be.wanna.Referencerback.repository.UserRepository;
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

    @Transactional
    public Long create(String login, CollectionDTOIn dto){
//        if(dto.photos().isEmpty()) throw new RuntimeException("Photos must not be empty or null.");
        User user = checkUser(login);

        Set<Photo> photos = dto.photos() == null ? new HashSet<>() : dto.photos().stream()
                .map(this::convertPhotoDtoPersist)
                .collect(Collectors.toSet());

        UserCollection collection = new UserCollection(
                dto.name(),
                dto.description(),
                photos
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
                dto.description(),
                dto.photos()==null ?
                        Collections.emptySet()
                        :
                        dto.photos().stream().map(this::convertPhotoDtoPersist).collect(Collectors.toSet())
        );

        collection.setId(saved.getId());
        collection.setUser(user);

        return repository.save(collection).getId();

    }

    public void delete(String login, Long id){
        User user = checkUser(login);
        UserCollection col = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found."));

        repository.delete(col);
    }

    @Transactional
    public void addPhotos(String login, Long id, AlbumCollectionDTO dto){
        User user = checkUser(login);
        UserCollection collection = repository.findByUserAndId(user, id);
        if(collection == null) throw  new RuntimeException("Collection not found.");

        if(dto.photos() != null)
            dto.photos().forEach(ph -> collection.addPhoto(
                    convertPhotoDtoPersist(
                            convertPhotoByCodeDtoToPhotoDto(ph))
                    )
            );

        UserCollection saved = repository.save(collection);

        collection.getPhotos().forEach(ph -> {
            ph.addCollection(saved);
        });

        photoRepository.saveAll(collection.getPhotos());
    }

    private CollectionDTOOut convertCollection(UserCollection collection) {
        return new CollectionDTOOut(
                collection.getId(),
                collection.getName(),
                collection.getDescription()
        );
    }

    private PhotoDTO convertPhotoByCodeDtoToPhotoDto(PhotoByPageDTO dto) {
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
                dto.mature()
        );

        return persist ? persistPhoto(photo) : photo ;
    }

    private Photo persistPhoto(Photo photo) {
        Optional<Photo> opt = photoRepository.findPhotoByCode(photo.getCode());

        if(opt.isEmpty()) return photoRepository.save(photo);

        Photo ph = opt.get();

        if(ph.getCode()==null && photo.getCode()!=null) ph.setCode(photo.getCode());

        return ph;
    }

    private User checkUser(String login){
        User user =  userRepository.findByLogin(login);
        if(user==null) throw new RuntimeException("User not found.");
        return user;
    }
}
