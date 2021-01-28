<template>
  <div>
    <modal v-if="showSignUpPopup" @close="closeSignUpPopup">
        <template slot="header">
          <h4 class="modal-title">회원가입</h4>
          <md-button class="md-simple md-just-icon md-round modal-default-button" @click="closeSignUpPopup">
            <md-icon>clear</md-icon>
          </md-button>
        </template>

        <template slot="body">
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
          <md-field class="md-form-group" slot="inputs">
            <md-icon>face</md-icon>
            <label>이름</label>
            <md-input v-model="name" type="email"></md-input>
          </md-field>
          <md-field class="md-form-group" slot="inputs">
            <md-icon>face</md-icon>
            <label>닉네임</label>
            <md-input v-model="nickname" type="email"></md-input>
          </md-field>
        </template>

        <template slot="footer">
          <md-button class="md-simple md-success md-lg" @click="onConfirm">완료</md-button>
          <md-button class="md-danger md-simple md-lg" @click="closeSignUpPopup">취소</md-button>
        </template>
      </modal>
  </div>
</template>

<script>
import ApiClient, { API } from 'api/client';
import DialogUtil from '@/utils/dialog';

export default {
  name: 'SignUpPopup',
  mixins: [ApiClient],
  props: {
    showSignUpPopup: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      email: '',
      password: '',
      name: '',
      nickname: '',
    };
  },
  computed: {},
  methods: {
    onConfirm() {
      if (!this.email || !this.password || !this.name || !this.nickname) {
        const msg = !this.email ? '이메일 주소를 입력하세요.' : (!this.password ? '비밀번호를 입력하세요.' : (!this.name ? '이름을 입력하세요.' : '닉네임을 입력하세요.'));
        // TODO: 템플릿에 맞는 에러메시지 alert
        DialogUtil.alert(msg);
        return;
      }

      const emailRegex = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
      if (!emailRegex.test(this.email)) {
        DialogUtil.alert('이메일 형식이 올바르지 않습니다.');
        return;
      }

      const pwdRegex =  /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{9,}$/;
      if (!pwdRegex.test(this.password)) {
        DialogUtil.alert('비밀번호는 영문 대/소문자와 특수문자 3개를 조합하여 9자 이상이어야 합니다.');
        return;
      }

      if (this.nickname.length < 2 || this.nickname.length > 8) {
        DialogUtil.alert('닉네임 길이는 2글자 이상 8글자 이하여야 합니다.');
        return;
      }

      this.$emit('confirm', { email: this.email, password: this.password, name: this.name, nickname: this.nickname, loginType: 'LOGTP_EMAIL' });
    },
    closeSignUpPopup() {
      this.initData();
      this.$emit('close');
    },
    initData() {
      this.email = '';
      this.password = '';
      this.name = '';
      this.nickname = '';
    },
  },
};
</script>
