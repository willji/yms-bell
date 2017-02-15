export const ADD_NOTIFICATION = 'ADD_NOTIFICATION';
export const REMOVE_NOTIFICATION = 'REMOVE_NOTIFICATION';

export const INFO = Symbol('info');
export const SUCCESS = Symbol('success');
export const WARNING = Symbol('warning');
export const ERROR = Symbol('error');


export function add(level = INFO, message = '', options = {}) {
  return dispatch => {
    return dispatch({
      type: ADD_NOTIFICATION,
      notification: Object.assign({}, {autoDismiss: true, ttl: 5}, options, {
        level: level,
        message
      })
    });
  };
}

export function remove(notification) {
  return dispatch => {
    return dispatch({
      notification,
      type: REMOVE_NOTIFICATION
    });
  };
}
