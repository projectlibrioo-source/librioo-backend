package org.example.projectlibrioo.Repository;

import org.example.projectlibrioo.Model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepo extends JpaRepository<Member, Integer> {

    Member findUserByLibraryID(int libraryID);

    @Query("SELECT m FROM Member m WHERE m.fullName = :fullname OR m.libraryID = :libraryid")
    List<Member> findAllMembersByKeyword(@Param("fullname") String fullname,
                                         @Param("libraryid") Integer libraryid);
}
