package com.example.oneuiapp;

public class ScrollListItem {
    
    private String title;
    private String description;
    private int iconResource;
    private boolean showChevron;
    private boolean isEnabled;
    private Object tag; // For additional data storage
    
    // Default constructor
    public ScrollListItem() {
        this.title = "";
        this.description = "";
        this.iconResource = 0;
        this.showChevron = true;
        this.isEnabled = true;
        this.tag = null;
    }
    
    // Full constructor
    public ScrollListItem(String title, String description, int iconResource, boolean showChevron, boolean isEnabled) {
        this.title = title != null ? title : "";
        this.description = description != null ? description : "";
        this.iconResource = iconResource;
        this.showChevron = showChevron;
        this.isEnabled = isEnabled;
        this.tag = null;
    }
    
    // Simplified constructor without chevron and enabled parameters
    public ScrollListItem(String title, String description, int iconResource) {
        this(title, description, iconResource, true, true);
    }
    
    // Constructor with title only
    public ScrollListItem(String title) {
        this(title, "", 0, true, true);
    }
    
    // Getters
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getIconResource() {
        return iconResource;
    }
    
    public boolean isShowChevron() {
        return showChevron;
    }
    
    public boolean isEnabled() {
        return isEnabled;
    }
    
    public Object getTag() {
        return tag;
    }
    
    // Setters
    public void setTitle(String title) {
        this.title = title != null ? title : "";
    }
    
    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }
    
    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }
    
    public void setShowChevron(boolean showChevron) {
        this.showChevron = showChevron;
    }
    
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
    
    public void setTag(Object tag) {
        this.tag = tag;
    }
    
    // Helper methods
    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }
    
    public boolean hasIcon() {
        return iconResource != 0;
    }
    
    public boolean hasTag() {
        return tag != null;
    }
    
    // Override toString for debugging
    @Override
    public String toString() {
        return "ScrollListItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", iconResource=" + iconResource +
                ", showChevron=" + showChevron +
                ", isEnabled=" + isEnabled +
                ", hasTag=" + hasTag() +
                '}';
    }
    
    // Override equals and hashCode for proper comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        ScrollListItem that = (ScrollListItem) obj;
        
        if (iconResource != that.iconResource) return false;
        if (showChevron != that.showChevron) return false;
        if (isEnabled != that.isEnabled) return false;
        if (!title.equals(that.title)) return false;
        if (!description.equals(that.description)) return false;
        
        return tag != null ? tag.equals(that.tag) : that.tag == null;
    }
    
    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + iconResource;
        result = 31 * result + (showChevron ? 1 : 0);
        result = 31 * result + (isEnabled ? 1 : 0);
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }
          }
