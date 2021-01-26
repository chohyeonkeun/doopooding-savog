export default {
  login: ({ commit }, userInfo) => {
    const { userId, userName, userEmail, userNickname, authToken, userRoles } = userInfo;

    commit('SET_USER_ID', userId);
    commit('SET_USER_NAME', userName);
    commit('SET_USER_EMAIL', userEmail);
    commit('SET_USER_NICKNAME', userNickname);
    commit('SET_AUTH_TOKEN', authToken);
    commit('SET_USER_ROLES', userRoles);
    commit('SET_LOGGED', true);
  },
  logout: ({ commit }) => {
    commit('SET_USER_ID', '');
    commit('SET_USER_NAME', '');
    commit('SET_USER_EMAIL', '');
    commit('SET_USER_NICKNAME', '');
    commit('SET_AUTH_TOKEN', '');
    commit('SET_USER_ROLES', []);
    commit('SET_LOGGED', false);
  },
};
