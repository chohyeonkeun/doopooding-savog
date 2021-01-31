# 세이보그 서비스
유기된 애완동물들의 프로필 정보를 제공해주는 플랫폼 서비스입니다.

## 적용 기술
##### 프론트엔드
- 언어 - *javascript*
- 프레임워크 - *vue.js*
- 통신 라이브러리 - *axios*
- 상태관리 라이브러리 - *vuex*

##### 백엔드
- 언어 - *kotlin*
- 프레임워크 - *spring boot*
- SQL 프레임워크 - *exposed*

##### 데이터베이스
- mySQL (로컬), mariaDB (RDS)

##### CICD
- travis CI, CodeDeploy

##### Deploy
- aws S3
- aws EC2
- aws RDS
- aws Route53
 

## 주요 기능
#### 로그인 및 권한 설정
- jwt & Spring security
```
[회원가입]
1. 이메일, 비밀번호, 이름, 닉네임 정보를 받아 회원가입 
2. 일반회원으로 roles 생성하여 유저 권한 테이블(= user_table)에 매핑 

[로그인]
1. 유저정보로 유저테이블에 저장된 roles 확보
2. email을 claim subject로 설정하고, email과 roles로 jwt 토큰 생성  
  > 토큰 생성 상세 코드
    fun createToken(userPk: String, roles: List<String>): String {
        val claims = Jwts.claims().setSubject(userPk) // JWT payload에 저장
        claims["roles"] = roles // 정보는 key, value 쌍으로 저장된다.
        val now = Date()
        return Jwts.builder()
            .setClaims(claims) // 정보 저장
            .setIssuedAt(now) // 토큰 발행 시간 정보
            .setExpiration(Date(now.time + tokenValidTime)) // 토큰 유효 시간 설정
            .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘(해싱)과 signature에 들어갈 secret값 세팅
            .compact()
    }

[헤더 토큰 파싱]
1. JwtAuthenticationFilter에서 헤더의 token 확인
2. 유효한 토큰인지 판별하여 토큰으로부터 유저 정보 확보(토큰 파싱하여 이메일주소 확보)
3. 이메일주소로 Authentication 객체 확보
4. SecurityContext에 Authentication 객체 저장

[Spring Security 에서 uri path별 권한 처리]
- WebSecurityConfigurerAdapter 상속받아 configure 함수 오버라이드하여 path별 권한 설정
  > path별 권한 처리 상세코드
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.httpBasic().disable() // rest api만을 고려하여 기본 설정 해제
            .csrf().disable() // csrf 보안 토큰 disable 처리
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 미사용
            .and()
            .authorizeRequests() // 요청에 대한 사용권한 체크
            .antMatchers(HttpMethod.POST, "/v1/pets", "/v1/pet/histories", "/v1/pet/diseases", "/v1/pet/treatmentHistories", "/v1/sponsorshipFees", "/v1/sponsorshipFee/transaction/histories").hasAnyAuthority(Codes.UserRoleType.ADMIN.value, Codes.UserRoleType.MANAGER.value)
            .antMatchers(HttpMethod.PUT, "/v1/pets", "/v1/pet/diseases", "/v1/pet/treatmentHistories", "/v1/sponsorshipFees", "/v1/sponsorshipFee/transaction/histories").hasAnyAuthority(Codes.UserRoleType.ADMIN.value, Codes.UserRoleType.MANAGER.value)
            .antMatchers(HttpMethod.DELETE, "/v1/sponsorshipFees").hasAnyAuthority(Codes.UserRoleType.ADMIN.value, Codes.UserRoleType.MANAGER.value)
            .anyRequest().permitAll() // 그 외 나머지 요청은 누구나 접근 가능
            .and()
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다.
    }
```

#### 파일 업로드
```
1. 파일 업로드 요청
2. 요구되는 파일 확장자 타입에 맞는 확장자인지 유효성 검사
3. 파일명, 파일유형, 개발모드 여부 정보로 key 생성 (extension, UUID, 현재일자로 구성된 key)
4. 해당 파일으로 된 FileInputStream 객체 생성하여 버킷명과 key, metadata로 AmazonS3 객체 주입
  > AmazonS3 객체 주입 
    FileInputStream(file).use {
        amazonS3Client.putObject(PutObjectRequest(버킷명, key, it, metadata))
    } 
```
#### 배포
```

``` 