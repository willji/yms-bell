/**
 * Created by xuemingli on 2016/10/18.
 */
import Guid from 'guid';
import {SET_NAVIGATION} from '../actions/navigation';

const initialState = {
  key: 'item',
  items: [],
  id: Guid.EMPTY
};

export default function (state = initialState, action = {}) {
  switch (action.type) {
    case SET_NAVIGATION:
      return Object.assign({}, state, action.nav, {id: Guid.raw()});
    default:
      return state;
  }
}
