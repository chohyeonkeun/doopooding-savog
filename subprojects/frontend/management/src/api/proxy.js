export default (methods, client) => new Proxy(
  methods,
  {
    get(target, propKey) {
      const origMethod = target[propKey];
      return function (...args) {
        return origMethod.apply(this, [client, ...args]);
      };
    },
  },
);
