package pet.project.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pet.project.blog.enums.RoleEnum;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor //
@AllArgsConstructor
@Entity
@Table(name="roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique=true)
    private RoleEnum role;

    @ManyToMany(mappedBy="roles")
    private List<User> users;
}
