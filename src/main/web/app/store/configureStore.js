import { createStore, applyMiddleware, compose } from 'redux';
import thunk from 'redux-thunk';
import createLogger from 'redux-logger';
import api from '../middleware/api';
import rootReducer from '../reducers';

let finalCreateStore = compose(
  applyMiddleware(thunk, api),
  applyMiddleware(createLogger()),
)(createStore);

if (process.env.NODE_ENV === 'production') {
  finalCreateStore = compose(
    applyMiddleware(thunk, api),
  )(createStore);
}

export default function configureStore(initialState) {
  const store = finalCreateStore(rootReducer, initialState);

  if (module.hot) {
    module.hot.accept('../reducers', () => {
      const nextRootReducer = require('../reducers');
      store.replaceReducer(nextRootReducer);
    });
  }
  return store;
}
