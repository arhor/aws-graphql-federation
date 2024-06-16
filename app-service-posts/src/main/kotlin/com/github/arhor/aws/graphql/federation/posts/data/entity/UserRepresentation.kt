package com.github.arhor.aws.graphql.federation.posts.data.entity

import com.github.arhor.aws.graphql.federation.starter.core.data.Features
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.EnumSet
import java.util.UUID

@Table(UserRepresentation.TABLE_NAME)
@Immutable
data class UserRepresentation @PersistenceCreator constructor(
    @Id
    @Column("id")
    private val id: UUID,

    @Column("features")
    val features: UserFeatures = UserFeatures(),

    @Transient
    val shouldBePersisted: Boolean = false,
) : Persistable<UUID> {

    override fun getId(): UUID = id

    override fun isNew(): Boolean = shouldBePersisted

    fun togglePosts(): UserRepresentation {
        return copy(features = features.toggle(UserFeature.POSTS_DISABLED))
    }

    fun postsDisabled(): Boolean {
        return features.check(UserFeature.POSTS_DISABLED);
    }

    enum class UserFeature {
        POSTS_DISABLED,
    }

    class UserFeatures(items: EnumSet<UserFeature>) : Features<UserFeatures, UserFeature>(items) {
        constructor()
            : this(EnumSet.noneOf(UserFeature::class.java))

        constructor(feature: UserFeature, vararg features: UserFeature)
            : this(EnumSet.of(feature, *features))

        override fun create(items: EnumSet<UserFeature>) = UserFeatures(items)
    }

    companion object {
        const val TABLE_NAME = "user_representations"
    }
}
