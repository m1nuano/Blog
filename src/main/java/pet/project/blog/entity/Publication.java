package pet.project.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private String tag;
    private String creator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;  // Publication creator

    public String getUsername() {
        return user.getUsername();
    }

    public Publication(String text, String tag, User user) {
        this.text = text;
        this.tag = tag;
        this.user = user;
    }

    public Publication(String newText, String newTag){
        this.text = newText;
        this.tag = newTag;
    }


}

