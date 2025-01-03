package com.frozenlake.model;

public abstract class AbstractGameEntity {
    protected Position position;
    protected final String id;
    
    protected AbstractGameEntity(String id, Position position) {
        this.id = id;
        this.position = position;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }
    
    public String getId() {
        return id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AbstractGameEntity)) return false;
        AbstractGameEntity other = (AbstractGameEntity) obj;
        return id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    public abstract String getDisplayName();
    
    public abstract boolean isActive();
} 