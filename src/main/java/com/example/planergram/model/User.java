package com.example.planergram.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_login_id", nullable = false, length = 30, unique = true)
    private String loginId;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30, unique = true)
    private String nickname;

    @OneToOne(mappedBy = "user", orphanRemoval = true)
    @JoinColumn(name="user_id")
    private UserInfo userInfo;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<StayLike> stayLikeList;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<StayLike> rentcarLikeList;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Post> postList;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reply> replyList;
}