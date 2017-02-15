package com.ymatou.op.yms.bell.domain

import com.avaje.ebean.Model
import org.hibernate.validator.constraints.Email
import javax.persistence.*

/**
 * Created by xuemingli on 16/9/8.
 */
@Entity
@Table(name = "users")
class User (
        @Id
        var id: Long? = null,
        @Column(length = 128, nullable = false)
        var name: String,
        @Column(length = 64, nullable = true, unique = true)
        @Email
        var email: String,
        @Column(length = 24, nullable = true)
        var mobile: String? = null,
        @Column(name = "is_new")
        var isNew: Boolean = true,
        @ManyToMany
        @JoinTable(name = "user_role", joinColumns = arrayOf(JoinColumn(name = "user_id")), inverseJoinColumns = arrayOf(JoinColumn(name = "role_id")))
        var roles: Set<Role> = setOf()
): Model() {

}