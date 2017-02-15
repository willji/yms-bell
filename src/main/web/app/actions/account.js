/**
 * Created by xuemingli on 16/9/12.
 */
import { PAYLOAD } from '../middleware/api';
import * as utils  from './utils';

export const LOGIN_TYPE_SET = utils.createTypes('login');

export function login(username, password, redirect='/') {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: LOGIN_TYPE_SET,
      endpoint: '/v1/authentication/login',
      options: {
        method: 'POST',
        body: JSON.stringify({username, password, redirect})
      }
    }
  });
}


export const GET_MY_SUBSCRIPTIONS_TYPE_SET = utils.createTypes('get_my_subscriptions');

export function list_subscriptions(page = 1, size = 50) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: GET_MY_SUBSCRIPTIONS_TYPE_SET,
      endpoint: '/v1/user/subscriptions',
      params: {page, size}
    }
  })
}
