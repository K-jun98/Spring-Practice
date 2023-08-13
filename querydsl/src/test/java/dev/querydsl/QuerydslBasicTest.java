package dev.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.querydsl.entity.Member;
import dev.querydsl.entity.QMember;
import dev.querydsl.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
import static dev.querydsl.entity.QMember.member;
import static dev.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;
    @PersistenceUnit
    EntityManagerFactory emf;

    @Test
    public void fetchJoinUse() throws Exception {
        em.flush();
        em.clear();
        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();
        boolean loaded =
                emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 적용").isTrue();
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    public void subQueryGoe() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                )).fetch();

        assertThat(result).extracting("age")
                .containsExactly(30, 40);
    }


    /*
    1. 회원 나이 내림차순 desc
    2. 회원 이름 올림차순 asc
    단 2에서 회원 이름이 없으면 마지막에 출력
     */
    /*

    결과 조회
    fetch(): 리스트 조회, 데이터 없으면 빈 리스트 반환
    fetchOne(): 단 건 조회
        결과가 없으면 :null
        결과가 둘 이상이면 : NonUniqueResultException
    fetchFirst(): limit(1).fetchOne()
    fetchResults(): 페이징 정보 포함, total count 쿼리 추가 실행
    fetchCount(): count 쿼리로 변경해서 count수 조회

    */

    /**
     * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and
     * t.name='teamA'
     */
    @Test
    void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory.select(member, team).from(member).leftJoin(member.team, team).on(team.name.eq("teamA")).fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @BeforeEach
    void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        Member member5 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);
    }

    @Test
    void startJPQL() {
        String qlString = """
                select m from Member m
                where m.username = :username
                """;

        Member findMember = em.createQuery(qlString, Member.class).setParameter("username", "member1").getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void startQuerydsl() {
        Member findMember = queryFactory.select(member).from(member).where(member.username.eq("member1")).fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void search() {
        Member findMember = queryFactory.selectFrom(member).where(member.username.eq("member1"), member.age.eq(10)).fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    void query() {
        List<Member> fetch = queryFactory.selectFrom(member).fetch();

        Member findMember1 = queryFactory.selectFrom(member).fetchOne();

        Member findMember2 = queryFactory.selectFrom(member).fetchFirst();

        // Deprecated
        queryFactory.selectFrom(member).fetchResults();

        //Deprecated
        queryFactory.selectFrom(member).fetchCount();
    }

    @DisplayName("정렬")
    @Test
    void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory.selectFrom(member).where(member.age.eq(100)).orderBy(member.age.desc(), member.username.asc().nullsLast()).fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @DisplayName("페이징")
    @Test
    void paging1() {
        List<Member> result = queryFactory.selectFrom(member).orderBy(member.username.desc()).offset(1).limit(2).fetch();

    }

    @DisplayName("페이징2")
    @Test
    void 페이징2() {
        queryFactory.selectFrom(member).orderBy(member.username.desc()).offset(1).limit(2).fetch();
    }

    @DisplayName("")
    @Test
    void 집합() {
        List<Tuple> result = queryFactory.select(member.count(), member.age.sum(), member.age.avg(), member.age.max(), member.age.min()).from(member).fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    @DisplayName("")
    @Test
    void groupBy() {
        List<Tuple> result = queryFactory.select(team.name, member.age.avg()).from(member).join(member.team, team).groupBy(team.name).orderBy(team.name.asc()).fetch();

        Tuple team1 = result.get(0);
        Tuple team2 = result.get(1);

        assertThat(team1.get(team.name)).isEqualTo("teamA");
        assertThat(team1.get(member.age.avg())).isEqualTo(15);
        assertThat(team2.get(team.name)).isEqualTo("teamB");
        assertThat(team2.get(member.age.avg())).isEqualTo(35);
    }

    @Test
    void theta_join() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        List<Member> result = queryFactory.select(member).from(member, team).where(member.username.eq(team.name)).fetch();
        assertThat(result).extracting("username").containsExactly("teamA", "teamB");
    }

    @Test
    void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded =
                emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    void subQuery() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                )).fetch();
        assertThat(result).extracting("age")
                .containsExactly(40);
    }

    @DisplayName("Case문")
    @Test
    void caseTest() {
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
    }

    @DisplayName("프로젝션필드")
    @Test
    void 프로젝션필드() {
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        System.out.println(result.size());
    }

    @DisplayName("distinct")
    @Test
    void distinctTest() {
        List<Member> result = queryFactory
                .select(member)
                .from(member)
                .fetch();

        System.out.println(result.size());
    }

    @Test
    void 동적쿼리_BooleanBuilder() throws Exception {
        String usernameParam = "member1";
        Integer ageParam = 10;
        List<Member> result = searchMember1(usernameParam, ageParam);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }
    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (usernameCond != null) {
            builder.and(member.username.eq(usernameCond));
        }
        if (ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }
        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    @Test
    public void 동적쿼리_WhereParam() throws Exception { String usernameParam = "member1";
        Integer ageParam = 10;
        List<Member> result = searchMember2(usernameParam, ageParam);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }
    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(member)
                .where(usernameEq(usernameCond), ageEq(ageCond))
                .fetch();
    }
    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? member.username.eq(usernameCond) : null;
    }
    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }



    public static class MemberDto {
        private String username;
        private int age;

        public MemberDto() {
        }

        public MemberDto(String username, int age) {
            this.username = username;
            this.age = age;
        }

        @Override
        public String toString() {
            return "MemberDto{" +
                    "username='" + username + '\'' +
                    ", age=" + age +
                    '}';
        }

    }


}
