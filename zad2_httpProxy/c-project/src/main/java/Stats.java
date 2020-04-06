
public class Stats {

    private String website;
    private Integer sent;
    private Integer received;
    private Integer request;

    public Stats(String website, Integer sent, Integer received, Integer request) {
        this.website = website;
        this.sent = sent;
        this.received = received;
        this.request = request;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Integer getSent() {
        return sent;
    }

    public void setSent(Integer sent) {
        this.sent = sent;
    }

    public Integer getReceived() {
        return received;
    }

    public void setReceived(Integer received) {
        this.received = received;
    }

    public Integer getRequest() {
        return request;
    }

    public void setRequest(Integer request) {
        this.request = request;
    }
}
