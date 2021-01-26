const alert = (message) => {
  window.alert(message);
};

const confirm = async(message, callback) => {
  const bool = window.confirm(message);
  if (bool) return await callback();
  else return bool;
};

export default { alert, confirm };
