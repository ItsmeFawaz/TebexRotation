package me.bottleofglass.tebexrotation.data;

public class Package {
    private String id;
    private boolean disabled;
    private String categoryId;

    public Package(String id, boolean disabled, String categoryId) {
        this.id = id;
        this.disabled = disabled;
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public String getCategoryId() {
        return categoryId;
    }
}
