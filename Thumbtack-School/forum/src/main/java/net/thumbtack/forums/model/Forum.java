package net.thumbtack.forums.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.thumbtack.forums.model.enums.ForumType;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Forum {

    private int id;
    private String name;
    private User creator;
    private ForumType type;
    private boolean readOnly;
    private List<MessageTree> messages;

    public Forum() {
    }

    public Forum(String name, User creator, ForumType type) {
        this.name = name;
        this.creator = creator;
        this.type = type;
    }

    public Forum(int id, String name, User creator, ForumType type, boolean readOnly) {
        this(name, creator, type);
        this.id = id;
        this.readOnly = readOnly;
    }

    public Forum(int id, String name, User creator, ForumType type, boolean readOnly, List<MessageTree> messages) {
        this(name, creator, type);
        this.id = id;
        this.readOnly = readOnly;
        this.messages = messages;
    }
}
