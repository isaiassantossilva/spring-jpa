# Estudo de Spring Data JPA

Catálogo de exemplos de uso do JPA / Spring Data JPA, cada tópico em um pacote
com sua entidade, repositório e um teste `@DataJpaTest` que demonstra o
comportamento. Rode tudo com:

```bash
./gradlew test
```

## Mapa dos exemplos

| Pacote | Tópico | Teste |
|---|---|---|
| `basics` | `@Entity`, `@Table`, `@Column` (nullable/length/unique/precision), `@Enumerated`, `@Lob`, `@Transient`, CRUD do `JpaRepository`, dirty checking | `ProductRepositoryTest` |
| `ids` | `GenerationType.IDENTITY`, `SEQUENCE` (+`@SequenceGenerator`), `UUID`; chaves compostas com `@EmbeddedId` e `@IdClass` | `IdGenerationTest` |
| `embedded` | `@Embeddable`/`@Embedded`, `@AttributeOverrides`, query method navegando no embeddable | `EmbeddedMappingTest` |
| `relationships.onetoone` | `@OneToOne` bidirecional, lado dono vs `mappedBy`, cascade, orphanRemoval | `OneToOneTest` |
| `relationships.onetomany` | `@OneToMany`/`@ManyToOne`, helpers de sincronização, LAZY, N+1 com `JOIN FETCH` e `@EntityGraph`, cascade remove | `OneToManyTest` |
| `relationships.manytomany` | `@ManyToMany` com `@JoinTable`, matrícula/desmatrícula sem apagar o outro lado | `ManyToManyTest` |
| `relationships.mapsid` | `@OneToOne` com chave derivada (`@MapsId` / shared primary key) | `MapsIdTest` |
| `relationships.associationentity` | Many-to-many com colunas extras: entidade associativa com `@EmbeddedId` + `@MapsId` | `AssociationEntityTest` |
| `relationships.elementcollection` | `@ElementCollection` com `Set` e `Map` (`@CollectionTable`, `@MapKeyColumn`) | `ElementCollectionTest` |
| `inheritance.singletable` | Herança `SINGLE_TABLE` + `@DiscriminatorColumn`/`@DiscriminatorValue`, consulta polimórfica, repositório de subclasse | `InheritanceTest` |
| `inheritance.joined` | Herança `JOINED` (uma tabela por classe) | `InheritanceTest` |
| `inheritance.tableperclass` | Herança `TABLE_PER_CLASS` (tabela só nas concretas, consulta via UNION) | `TablePerClassTest` |
| `secondarytable` | `@SecondaryTable`: uma entidade em duas tabelas | `SecondaryTableTest` |
| `collections` | `@OrderColumn` (posição persistida) vs `@OrderBy` (ORDER BY no load) | `CollectionOrderingTest` |
| `auditing` | `@MappedSuperclass`, `@CreatedDate`/`@LastModifiedDate` e `@CreatedBy`/`@LastModifiedBy` com `AuditorAware` | `AuditingTest` |
| `queries` | Query methods derivados (Containing, Between, In, Top, count/exists/delete...) | `DerivedQueryMethodsTest` |
| `queries` | `@Query` JPQL (parâmetros nomeados/posicionais, agregação, constructor expression), SQL nativo, `@Modifying` | `JpqlAndNativeQueryTest` |
| `queries` | `Sort`, `Pageable`/`Page`, `Slice` | `PaginationAndSortingTest` |
| `queries` | Projeções: interface (closed/open com SpEL), DTO record, projeção dinâmica | `ProjectionsTest` |
| `queries` | `Specification` (Criteria API) combináveis com and/or | `SpecificationsTest` |
| `queries` | Query by Example (`Example`, `ExampleMatcher`) | `QueryByExampleTest` |
| `queries` | Repositório customizado (fragment `EmployeeRepositoryCustom` + `Impl`) com Criteria API crua | `CustomRepositoryTest` |
| `queries` | `@NamedQuery`, `@NamedNativeQuery` + `@SqlResultSetMapping` | `NamedQueriesTest` |
| `queries` | `Stream<T>`, `Limit` dinâmico e Scroll API (keyset) | `StreamLimitScrollTest` |
| `queries` | Função do banco via `CREATE ALIAS` + `CALL` (stored procedure no H2) | `StoredProcedureTest` |
| `queries` | QueryDSL: `QuerydslPredicateExecutor`, `JPAQueryFactory`, projeções e agregação type-safe | `QueryDslTest` |
| `converters` | `AttributeConverter`: enum → código (`autoApply`) e `List<String>` → CSV (`@Convert`) | `AttributeConverterTest` |
| `hibernatefeatures` | Extensões do Hibernate: `@Formula`, `@NaturalId`, `@Filter`, `@SQLRestriction`, `@SoftDelete` | `HibernateFeaturesTest` |
| `hibernatefeatures` | `@BatchSize` contra N+1, medido com `Statistics` | `BatchSizeTest` |
| `cache` | Cache de segundo nível (`@Cacheable` + `@Cache`) e query cache com JCache/EhCache | `SecondLevelCacheTest` |
| `transactions` | `@Transactional` na camada de serviço: rollback rules, propagações (REQUIRES_NEW, MANDATORY, NEVER, NESTED), `readOnly`, rollback-only e a pegadinha da auto-invocação | `TransactionalServiceTest` |
| `locking` | Lock otimista com `@Version` | `OptimisticLockingTest` |
| `locking` | Lock pessimista (`@Lock` / SELECT FOR UPDATE) com transações concorrentes reais | `PessimisticLockingTest` |
| `lifecycle` | Callbacks `@PrePersist`/`@PostPersist`/`@PreUpdate`/`@PreRemove`/`@PostLoad` | `LifecycleCallbacksTest` |
| `lifecycle` | Estados da entidade: transient/managed/detached/removed, `persist`, `detach`, `merge`, `remove`, `getReference` | `EntityManagerStatesTest` |

## Notas

- **Spring Boot 4.1**: os pacotes de teste foram modularizados —
  `@DataJpaTest` vem de `org.springframework.boot.data.jpa.test.autoconfigure`
  e `TestEntityManager` de `org.springframework.boot.jpa.test.autoconfigure`.
- **`@DataJpaTest`** sobe só a camada JPA com H2 em memória; cada teste roda
  numa transação revertida ao final.
- **H2 fixado em 2.3.232**: a 2.4.240 tem um bug em que check constraints
  (geradas pelo Hibernate para colunas discriminadoras/enums) falham quando
  avaliadas em uma conexão diferente da que as criou
  ([h2database#4320](https://github.com/h2database/h2database/issues/4320)).
- O H2 console fica disponível em `/h2-console` ao rodar a aplicação
  (`./gradlew bootRun`).
- **`@Procedure`**: o H2 não suporta parâmetros OUT (suas "procedures" são
  funções Java via `CREATE ALIAS`), então o exemplo usa `CALL` nativo; em
  Postgres/MySQL o mesmo caso usaria `@Procedure(procedureName = ...)`.
- **Cache de segundo nível**: habilitado apenas no `SecondLevelCacheTest`
  (via `properties` do `@DataJpaTest`), porque com a estratégia READ_WRITE
  uma entrada gravada na transação corrente não é legível por ela mesma —
  o cache só faz sentido entre transações, então o teste roda cada operação
  na sua própria transação (`@Transactional(NOT_SUPPORTED)`).
- **`@Transactional`**: os testes de transação desligam a transação do
  `@DataJpaTest` (`NOT_SUPPORTED`) — caso contrário todo o teste roda numa
  transação única e a semântica de commit/rollback dos serviços fica
  mascarada. Pontos que costumam surpreender: exceção *checked* não faz
  rollback por padrão; capturar a exceção de um colaborador REQUIRED não
  impede o rollback (`UnexpectedRollbackException`); e `this.metodo()` não
  passa pelo proxy, ignorando a propagação declarada.
- **QueryDSL**: as Q-classes (`QEmployee` etc.) são geradas pelo
  `querydsl-apt` em `build/generated/sources/annotationProcessor` durante a
  compilação — se não aparecerem na IDE, rode `./gradlew compileJava` e
  marque o diretório como generated sources root.
