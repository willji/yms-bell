/**
 * Created by xuemingli on 2016/10/18.
 */
export const SET_NAVIGATION = 'SET_NAVIGATION';

export function set(key, items = []) {
  return dispatch => {
    return dispatch({
      type: SET_NAVIGATION,
      nav: {key, items}
    })
  }
}
