package org.fullcycle.admin.catalog.infrastructure.video.persistence;

import org.fullcycle.admin.catalog.domain.castmember.CastMemberID;
import org.fullcycle.admin.catalog.domain.category.CategoryID;
import org.fullcycle.admin.catalog.domain.genre.GenreID;
import org.fullcycle.admin.catalog.domain.video.VideoPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface VideoRepository extends JpaRepository<VideoJpaEntity, String> {

    @Query(
        """
        select new org.fullcycle.admin.catalog.domain.video.VideoPreview(
            v.id as id,
            v.title as title,
            v.description as description,
            v.createdAt as createdAt,
            v.updatedAt as updatedAt
        )
        from Video v
            join v.categories categories
            join v.genres genres
            join v.castMembers castMembers
        where
            ( :terms is null or UPPER(v.title) like :terms )
        and
            ( :categories is null or categories.id.categoryId in :categories )
        and
            ( :genres is null or genres.id.genreId in :genres )
        and
            ( :castMembers is null or castMembers.id.castMemberId in :castMembers )
        """
    )
    Page<VideoPreview> findAll(
        @Param("terms") final String terms,
        @Param("categories") final Set<String> categories,
        @Param("genres") final Set<String> genres,
        @Param("castMembers") final Set<String> castMembers,
        final Pageable pageable
    );

}
