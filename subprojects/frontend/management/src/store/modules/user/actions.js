export default {
  setUserInfo: ({ commit }, userInfo) => {
    const { userId, userName, userEmail, userNickname, authToken, userRoles } = userInfo;

    commit('SET_USER_ID', userId);
    commit('SET_USER_NAME', userName);
    commit('SET_USER_EMAIL', userEmail);
    commit('SET_USER_NICKNAME', userNickname);
    commit('SET_AUTH_TOKEN', authToken);
    commit('SET_USER_ROLES', userRoles);
  },
};
