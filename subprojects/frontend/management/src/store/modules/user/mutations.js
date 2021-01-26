export default {
  SET_USER_ID: (state, userId) => {
    state.userId = userId;
  },
  SET_USER_NAME: (state, userName) => {
    state.userName = userName;
  },
  SET_USER_EMAIL: (state, userEmail) => {
    state.userEmail = userEmail;
  },
  SET_USER_NICKNAME: (state, userNickname) => {
    state.userNickname = userNickname;
  },
  SET_AUTH_TOKEN: (state, authToken) => {
    state.authToken = authToken;
  },
  SET_USER_ROLES: (state, userRoles) => {
    state.userRoles = userRoles;
  },
  SET_LOGGED: (state, logged) => {
    state.logged = logged;
  },
};
