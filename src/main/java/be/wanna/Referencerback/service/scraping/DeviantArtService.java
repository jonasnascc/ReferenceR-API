package be.wanna.Referencerback.service.scraping;

import be.wanna.Referencerback.dto.album.AlbumDTO;
import be.wanna.Referencerback.dto.AuthorProfileDTO;
import be.wanna.Referencerback.dto.CsrfResponseDTO;
import be.wanna.Referencerback.dto.photo.PhotoAlbumByPageDTO;
import be.wanna.Referencerback.dto.photo.PhotoDTO;
import be.wanna.Referencerback.dto.deviantArt.TagDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.DeviationDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.MediaDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.mediatype.MediaTypeDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.mediatype.Ss;
import be.wanna.Referencerback.dto.deviantArt.deviation.offset.OffSetDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.out.DeviationMediaDTO;
import be.wanna.Referencerback.dto.deviantArt.gallery.GalResultDTO;
import be.wanna.Referencerback.dto.deviantArt.gallery.GalleryInfoDTO;
import be.wanna.Referencerback.dto.deviantArt.gallery.ModuleDTO;
import be.wanna.Referencerback.dto.deviantArt.gallery.ProfileInfoDTO;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.Provider;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.photo.PhotoType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DeviantArtService {
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";

    private final String DEVIANT_ART_URL = "https://www.deviantart.com/";

    private final String DEVIANTART = "deviantart";

    private final String OFFSET_URL = "https://www.deviantart.com/_puppy/dashared/gallection/contents";

    private final String USER_GALLERY_INFO_URL = "https://www.deviantart.com/_puppy/dauserprofile/init/gallery";

    private final String USER_PROFILE_INFO_URL = "https://www.deviantart.com/_puppy/dauserprofile/init/about";
    //username?
    //csrf_token?

    private final String DEVIATION_DETAILS_URL = "https://www.deviantart.com/_puppy/dadeviation/init";
    //?deviationid=1011484517&username=Nightvenjer&type=art&include_session=false&csrf_token=

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    public List<AlbumDTO> findUserAlbums(String author){
        GalleryInfoDTO galInfo = getUserGalleryInfo(author);
        ModuleDTO moduleDTO = galInfo.gruser().page().modules().stream()
                .filter(mod -> mod.name().equals("folders")).findAny().orElse(null);

        List<AlbumDTO> albums = new ArrayList<>();
        if(moduleDTO != null){
            List<GalResultDTO> results = moduleDTO.moduleData().folders().results();
            if(results!=null){
                results.forEach(res -> {
                    String id  = Integer.toString(res.folderId());
                    if(id.equals("-1")) id = "all".concat(":").concat(author.toLowerCase());
                    else if(id.equals("-2")) id = "scraps".concat(":").concat(author.toLowerCase());

                    String albumUrl = "https://www.deviantart.com/" + author + "/gallery/" + id;
                    albums.add(
                            getAlbum(
                                    id,
                                    res.name(),
                                    albumUrl,
                                    getDeviationDTOPhotoDTO(res.thumb(), id, null,300),
                                    author,
                                    res.size()
                            )
                    );
                });
            }
        }
//
        return albums;
    }

    public Set<PhotoDTO> listAlbumPhotosByPage(String albumCode, String authorName, int pageNumber, int limitByPage, Integer maxThumbsize){
        return getAlbumDeviations(albumCode, authorName, pageNumber, limitByPage).stream()
                .map(dev -> getDeviationDTOPhotoDTO(dev, albumCode, pageNumber, maxThumbsize)).collect(Collectors.toSet());
    }

    private Set<DeviationDTO> getAlbumDeviations(String albumCode, String authorName, Integer page, Integer limitByPage){
        int offsetPg = (page - 1) * limitByPage;

        OffSetDTO offset = getOffSet(offsetPg, albumCode, authorName, limitByPage);

        Set<DeviationDTO> deviationsSet;
        if(offset!=null){
            deviationsSet = new HashSet<>(offset.getResults());
        }
        else return Collections.emptySet();

        return deviationsSet;
    }

    private GalleryInfoDTO getUserGalleryInfo(String authorName){
        CsrfResponseDTO csrfResponseDTO = getCsrfResponse(authorName);

        Map<String, String> params = new HashMap<>();
        params.put("username", authorName);
        params.put("deviations_limit", "1");
        params.put("with_subfolders", "true");
        params.put("csrf_token", csrfResponseDTO.csrfToken());
        try{
            Connection.Response  response = Jsoup.connect(USER_GALLERY_INFO_URL)
                    .userAgent(USER_AGENT)
                    .referrer(DEVIATION_DETAILS_URL)
                    .data(params)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .cookies(csrfResponseDTO.cookies())
                    .execute();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            GalleryInfoDTO galleryInfoDTO = gson.fromJson(response.body(), GalleryInfoDTO.class);

            return galleryInfoDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AuthorProfileDTO getAuthorProfile(String authorName){
        ProfileInfoDTO profileInfo = getUsersProfileInfo(authorName);
        if(profileInfo==null) return null;

        return new AuthorProfileDTO(
                profileInfo.owner().usericon(),
                profileInfo.pageExtraData().gruserTagline(),
                profileInfo.owner().username(),
                profileInfo.pageExtraData().stats().deviations(),
                profileInfo.pageExtraData().stats().watchers(),
                profileInfo.pageExtraData().stats().watching(),
                profileInfo.pageExtraData().stats().pageviews(),
                profileInfo.pageExtraData().stats().favourites()
        );
    }

    private ProfileInfoDTO getUsersProfileInfo(String authorName){
        CsrfResponseDTO csrfResponseDTO = getCsrfResponse(authorName);

        Map<String, String> params = new HashMap<>();
        params.put("username", authorName);
        params.put("csrf_token", csrfResponseDTO.csrfToken());

        try{
            Connection.Response  response = Jsoup.connect(USER_PROFILE_INFO_URL)
                    .userAgent(USER_AGENT)
                    .referrer(DEVIATION_DETAILS_URL)
                    .data(params)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .cookies(csrfResponseDTO.cookies())
                    .execute();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ProfileInfoDTO profileInfoDTO = gson.fromJson(response.body(), ProfileInfoDTO.class);

            return profileInfoDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TagDTO> getDeviationTagsByUrl(String deviationUrl) {
        try{
            Document doc = Jsoup.connect(deviationUrl)
                    .userAgent(USER_AGENT)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .get();

            List<Element> tagElements = doc.select("span._1nwad");

            List<TagDTO> tags = new ArrayList<>();
            if(tagElements.isEmpty()) return tags;

            tagElements.forEach(tag -> tags.add(new TagDTO(tag.text(), DEVIANT_ART_URL + "tag/" + tag.text())));

            return tags;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private List<OffSetDTO> getAllOffSets(String albumId, String authorName){
        List<OffSetDTO> list = new ArrayList<>();

        OffSetDTO offSet = getOffSet(0, albumId, authorName);
        list.add(offSet);

        System.out.println("Gettin' OffSets...");
        int count = 0;

        while(offSet!=null && offSet.hasMore()){

            System.out.print(count + " -> ");
            offSet = getOffSet(offSet.getNextOffset(), albumId, authorName);
            if(offSet != null) list.add(offSet);
            count++;

        }

        return list;
    }

    private OffSetDTO getOffSet(Integer number, String albumId, String authorName){
        return getOffSet(number, albumId, authorName, 60);
    }

    private OffSetDTO getOffSet(Integer number, String albumId, String authorName, Integer limit){
        CsrfResponseDTO csrfResponseDTO = getCsrfResponse(authorName);

        Document doc = null;
        try {
            Map<String, String> offSetParams = getOffSetParams(number, albumId, authorName, limit, csrfResponseDTO.csrfToken());

            doc = Jsoup.connect(OFFSET_URL)
                    .userAgent(USER_AGENT)
                    .referrer("https://www.deviantart.com/%s/gallery".formatted(authorName))
                    .data(offSetParams)
                    .method(org.jsoup.Connection.Method.GET)
                    .ignoreContentType(true)
                    .cookies(csrfResponseDTO.cookies())
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
            //return getOffSet(number, album);
        }

        if(doc == null) {
            System.out.println();
            throw new RuntimeException("Error while readin' the page.");
        }
        Elements elements = doc.getElementsByTag("body");

        String json = elements.text();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try{
//            System.out.println(json);
            return gson.fromJson(json, OffSetDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    private CsrfResponseDTO getCsrfResponse(String authorName) {
        try {
            org.jsoup.Connection.Response response = Jsoup.connect("https://www.deviantart.com/%s/gallery".formatted(authorName))
                    .userAgent(USER_AGENT)
                    .method(org.jsoup.Connection.Method.GET)
                    .execute();

            String startOfLine = response.body().substring(response.body().indexOf("__CSRF_TOKEN__"));
            String tokenLine = startOfLine.substring(0, startOfLine.indexOf(";"));

            return new CsrfResponseDTO(tokenLine.split("'")[1], response.cookies());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getOffSetParams(Integer number, String albumId, String authorName, Integer limit, String csrfToken){
        Map<String, String> params = new HashMap<>();

        params.put("username", authorName);
        params.put("type", "gallery");
        params.put("offset", Integer.toString(number));
        params.put("limit", Integer.toString(limit));

        if(albumId.startsWith("all"))
            params.put("all_folder", "true");
        else params.put("folderid", getAlbumIdStr(albumId));

        params.put("csrf_token", csrfToken);

        return params;

    }

    public DeviationMediaDTO getDeviationInfoByUrl(String url){
        try{
            Map<String, String> data = new HashMap<>();
            data.put("url", url);

            Connection.Response response = Jsoup.connect("https://backend.deviantart.com/oembed")
                    .data(data)
                    .ignoreContentType(true)
                    .execute();

            Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
            return gson.fromJson(response.body(), DeviationMediaDTO.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAlbumIdStr(String albumId){
        return albumId.replace(DEVIANTART.concat("-"), "");
    }

    private AlbumDTO getAlbum(String id, String name, String url, PhotoDTO thumbnail, String author, Integer photosNum){
        return new AlbumDTO(null ,id, name, url, thumbnail, author, DEVIANTART, photosNum);
    }

    private PhotoDTO getDeviationDTOPhotoDTO(DeviationDTO dto, String albumCode, Integer pageNumber, Integer maxThumbsize){
        PhotoDTO deviation = PhotoDTO.builder()
                .code(Long.toString(dto.getDeviationId()))
                .webPage(dto.getUrl())
                .title(dto.getTitle())
                .mature(dto.isMature())
                .matureLevel(dto.getMatureLevel())
                .license(dto.getLicense())
                .type(PhotoType.DEVIATION)
                .authorName(dto.getAuthor().getUsername())
                .photoAlbumByPage(new ArrayList<>(List.of(new PhotoAlbumByPageDTO(
                        pageNumber,
                        albumCode
                ))))
                .token(dto.getMedia().getToken() != null ? dto.getMedia().getToken().get(0) : null)
                .tokenExpireTime(dto.getMedia().getToken() != null ? Date.from(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(20).atZone(ZoneId.of("America/Sao_Paulo")).toInstant()) : null)
                .build();

        formatPublishedTime(dto.getPublishedTime(), deviation);

        deviation.setUrl(getDeviationDownloadUrl(dto));
        if(maxThumbsize!=null) deviation.setThumbUrl(getDeviationDownloadUrl(dto, maxThumbsize));

        return deviation;
    }

    private void formatPublishedTime(String dtoPublishedTime, PhotoDTO deviation) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        Date date = null;
        try {
            date = format.parse(dtoPublishedTime);
            deviation.setPublishedTime(date);
        } catch (ParseException ignored) {}
    }

    private String getDeviationDownloadUrl(DeviationDTO dto) {
        return getDeviationDownloadUrlByType(dto.getMedia());
    }

    private String getDeviationDownloadUrl(DeviationDTO dto, int maxThumbSize) {
        return getDeviationDownloadUrlByType(dto.getMedia(), maxThumbSize);
    }

    private String getDeviationDownloadUrlByType(MediaDTO media){
        return getDeviationDownloadUrlImpl(media, null, true);
    }

    private String getDeviationDownloadUrlByType(MediaDTO media, int maxHeight){
        return getDeviationDownloadUrlImpl(media,  maxHeight, false);
    }

    private String getDeviationDownloadUrlImpl(MediaDTO media, Integer maxHeight, boolean fullSize) {
        if(media.getBaseUri()==null || (maxHeight==null && !fullSize)) return "";

        boolean fullHeight = false;
        if(fullSize) fullHeight = fullSize;

        List<MediaTypeDTO> types = media.getTypes();

        int bestIndex = 0;
        for (int i = 1; i < types.size(); i++) {
            MediaTypeDTO currentType = validateType(types.get(i));
            MediaTypeDTO best = validateType(types.get(bestIndex));
            if( this.checkValidTypeDto(best) && this.checkValidTypeDto(currentType) ) {
                int typeHeight = currentType.getH();
                if (typeHeight > best.getH()) {
                    if(!fullHeight && typeHeight <= maxHeight){
                        bestIndex = i;
                    }
                    else if(fullHeight) {
                        bestIndex = i;
                    }
                }
            } else {
                bestIndex = i;
            }
        }
        if(!this.checkValidTypeDto(types.get(bestIndex))) return "";

        MediaTypeDTO type = types.get(bestIndex);
        String view = type.getC()==null ? "" : type.getC();
        String prettyName = media.getPrettyName();
        String token = media.getToken() != null ? ("?token=" + media.getToken().get(0)) : "";

        return media.getBaseUri().concat(view + token).replace("<prettyName>", prettyName);
    }

    public String getDeviationUrl(Photo deviation) {
        return getDeviationInfoByUrl(deviation.getWebPage()).url();
    }

    public String getDeviationToken(Photo deviation) {
        String url = getDeviationUrl(deviation);
        if(!url.contains("?token=")) return "";
        String urlStartingFromToken = url.substring(url.indexOf("?token=") + 7);
        if(!urlStartingFromToken.contains("&")) {
            return urlStartingFromToken;
        }

        return urlStartingFromToken.substring(0, urlStartingFromToken.indexOf("&"));
    }

    public PhotoDTO getDeviationWithToken(Photo deviation, String author) {
        deviation.setWebPage(getDeviationWebPageUrl(deviation.getUrl(), deviation.getCode(), author));
        if(deviation.getAuthor()==null) deviation.setAuthor(new Author(author, DEVIANT_ART_URL+author, new Provider(DEVIANTART)));
        deviation.setUrl(getDeviationUrl(deviation));

        return modelMapper.map(deviation, PhotoDTO.class);
    }

    private MediaTypeDTO validateType (MediaTypeDTO type) {
        if(!checkValidTypeDto(type)) {
            List<Ss> ssList = type.getSs();
            if(ssList == null ) return type;
            if(!ssList.isEmpty()){
                Ss ss = ssList.get(0);

                if(ss.getH() == null || ss.getW() == null) return type;
                type.setW(ss.getW());
                type.setH(ss.getH());
                type.setC(ss.getC());
            }
        }
        return type;
    }

    private boolean checkValidTypeDto (MediaTypeDTO type) {
        return type.getH()!=null || type.getW()!=null;
    }

    private String getAuthorProfileUrl(String author){
        return DEVIANT_ART_URL.concat(author);
    }

    private String getDeviationWebPageUrl(String title, String code, String author) {
        return "https://www.deviantart.com/"
                +author+
                "/art/"
                +title.replace(" " , "-") + "-" + code;
    }

}
