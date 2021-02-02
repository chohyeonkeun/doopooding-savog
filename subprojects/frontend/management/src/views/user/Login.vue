<template>
  <div class="wrapper">
    <div class="section page-header header-filter" :style="headerStyle">
      <div class="container">
        <div class="md-layout">
          <div
            class="md-layout-item md-size-33 md-small-size-66 md-xsmall-size-100 md-medium-size-40 mx-auto"
          >
            <login-card header-color="green">
              <h4 slot="title" :style="{ color: 'white' }"><b>로그인</b></h4>
              <md-button
                slot="buttons"
                href="javascript:void(0)"
                class="md-just-icon md-simple md-white"
              >
                <i class="fab fa-facebook-square"></i>
              </md-button>
              <md-button
                slot="buttons"
                href="javascript:void(0)"
                class="md-just-icon md-simple md-white"
              >
                <i class="fab fa-twitter"></i>
              </md-button>
              <md-button
                slot="buttons"
                href="javascript:void(0)"
                class="md-just-icon md-simple md-white"
              >
                <i class="fab fa-google-plus-g"></i>
              </md-button>
              <md-field class="md-form-group" slot="inputs">
                <md-icon>email</md-icon>
                <label>이메일</label>
                <md-input v-model="email" type="email"></md-input>
              </md-field>
              <md-field class="md-form-group" slot="inputs">
                <md-icon>lock_outline</md-icon>
                <label>비밀번호</label>
                <md-input v-model="password" type="password"></md-input>
              </md-field>
              <md-button slot="footer" class="md-simple md-success md-lg" @click="onConfirm">
                확인                
              </md-button>
              <md-button slot="footer" class="md-simple md-success md-lg" @click="openSignUpPopup">
                회원가입
              </md-button>
            </login-card>
          </div>
        </div>
      </div>
    </div>
    <sign-up-popup
      :show-sign-up-popup="showSignUpPopup"
      @confirm="signUp"
      @close="closeSignUpPopup"
    />
  </div>
</template>

<script>
import { LoginCard } from '@/components';
import ApiClient, { API } from 'api/client';
import DialogUtil from '@/utils/dialog';
import SignUpPopup from './popup/SignUpPopup';

export default {
  extends: ApiClient,
  components: {
    LoginCard,
    SignUpPopup,
  },
  bodyClass: 'login-page',
  data() {
    return {
      email: '',
      password: '',
      showSignUpPopup: false,
    };
  },
  props: {
    header: {
      type: String,
      default: require('@/assets/img/profile_city.jpg'),
    },
  },
  computed: {
    headerStyle() {
      return {
        backgroundImage: `url(${this.header})`,
      };
    },
  },
  methods: {
    async onConfirm() {
      if (!this.email || !this.password) {
        const msg = !this.email ? '아이디를 입력해주세요.' : '비밀번호를 입력해주세요.';
        // TODO: 템플릿에 맞는 에러메시지 alert
        DialogUtil.alert(msg);
        return;
      }

      const emailRegex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
      if (!emailRegex.test(this.email)) {
        DialogUtil.alert('이메일 형식이 올바르지 않습니다.');
        return;
      }

      const pwdRegex = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{9,}$/;
      if (!pwdRegex.test(this.password)) {
        DialogUtil.alert('비밀번호는 영문 대/소문자와 특수문자 3개를 조합하여 9자 이상이어야 합니다.');
        return;
      }

      const UserApi = this.getApi(API.USER);      
      // TODO: await 전후 loading 처리
      await UserApi.login({ email: this.email, password: this.password, loginType: 'LOGTP_EMAIL' })
        .then((res) => {
          this.$store.dispatch('user/login', res);
          localStorage.setItem('loginUserId', res.userId);
          localStorage.setItem('loginUsername', res.userNickname);
          this.$router.push('/');
          this.initData();
        })
        .catch((err) => {
          // TODO: 템플릿에 맞는 에러메시지 alert 
          // TODO: 아이디, 비밀번호 잘못 입력시, 처리
          DialogUtil.alert(err);
          this.initData();
        });
    },
    async signUp(data) {
      const UserApi = this.getApi(API.USER);
      await UserApi.join(data)
        .then(() => {
          // TODO: 템플릿에 맞는 에러메시지 alert
          DialogUtil.alert('세이보그 회원이 되신걸 축하드립니다.');
        })
        .catch((err) => {
          // TODO: 템플릿에 맞는 에러메시지 alert
          DialogUtil.alert(err);
        });
    },
    openSignUpPopup() {
      this.showSignUpPopup = true;
    },
    closeSignUpPopup() {
      this.showSignUpPopup = false;
    },
    initData() {
      this.email = '';
      this.password = '';
    },
  },
};
</script>

<style lang="css"></style>
