package dao;

public class PhotoDao {
    public int travelId;
    public int photoId;
    public String photoLocation;
    public String photoLatitude;
    public String photoLongitude;
    public String photoDateTime;
    public String photoMemo;
    public String photoUrl;
    public String photoCoverYn;
    public String createDate;
    public String updateDate;

    public int getTravelId() {
        return travelId;
    }

    public void setTravelId(int travelId) {
        this.travelId = travelId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getPhotoLocation() {
        return photoLocation;
    }

    public void setPhotoLocation(String photoLocation) {
        this.photoLocation = photoLocation;
    }

    public String getPhotoLatitude() {
        return photoLatitude;
    }

    public void setPhotoLatitude(String photoLatitude) {
        this.photoLatitude = photoLatitude;
    }

    public String getPhotoLongitude() {
        return photoLongitude;
    }

    public void setPhotoLongitude(String photoLongitude) {
        this.photoLongitude = photoLongitude;
    }

    public String getPhotoDateTime() {
        return photoDateTime;
    }

    public void setPhotoDateTime(String photoDateTime) {
        this.photoDateTime = photoDateTime;
    }

    public String getPhotoMemo() {
        return photoMemo;
    }

    public void setPhotoMemo(String photoMemo) {
        this.photoMemo = photoMemo;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoCoverYn() {
        return photoCoverYn;
    }

    public void setPhotoCoverYn(String photoCoverYn) {
        this.photoCoverYn = photoCoverYn;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
