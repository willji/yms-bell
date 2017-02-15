/**
 * Created by xuemingli on 16/5/25.
 */
import Guid from 'guid';
import {UNAUTHORIZED, FORBIDDEN} from '../middleware/api';

const initialState = {
  isAuthenticated: true,
  forbidden: false,
  user: null,
  id: Guid.EMPTY
};

export default function auth(state = initialState, action = {}) {
  switch (action.type) {
    case UNAUTHORIZED:
      return Object.assign({}, state, {isAuthenticated: false, id: Guid.raw()});
    case FORBIDDEN:
      return Object.assign({}, state, {forbidden: true, id: Guid.raw()});
    default:
      return initialState;
  }
}
