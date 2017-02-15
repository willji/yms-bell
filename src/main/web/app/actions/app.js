/**
 * Created by xuemingli on 2016/10/18.
 */
import {PAYLOAD} from '../middleware/api';
import * as utils from './utils';

export const APP_SEARCH_TYPE_SET = utils.createTypes("app_search");

export function searchApp(q, item) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: APP_SEARCH_TYPE_SET,
      endpoint: `/v1/app/search`,
      params: {q, item}
    }
  })
}

export const API_SEARCH_TYPE_SET = utils.createTypes("api_search");

export function searchApi(q, app, item) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: API_SEARCH_TYPE_SET,
      endpoint: '/v1/app/api/search',
      params: {q, app, item}
    }
  })
}

export const GET_APP_TYPE_SET = utils.createTypes('get_app');
export function getApp(name) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: GET_APP_TYPE_SET,
      endpoint: `/v1/app/${name}`
    }
  })
}

export const GET_API_TYPE_SET = utils.createTypes('get_api');
export function getApi(name, app) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: GET_API_TYPE_SET,
      endpoint: '/v1/app/api',
      params: {name, app}
    }
  })
}

export const ADD_ITEM_TYPE_SET = utils.createTypes('api_add_item');
export function addItem(name, app, item) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: ADD_ITEM_TYPE_SET,
      endpoint: '/v1/app/api/item',
      params: {name, app, item},
      options: {method: 'PUT'}
    }
  })
}

export const REMOVE_ITEM_TYPE_SET = utils.createTypes('api_remove_item');
export function removeItem(name, app, item) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: REMOVE_ITEM_TYPE_SET,
      endpoint: '/v1/app/api/item',
      params: {name, app, item},
      options: {method: 'DELETE'}
    }
  })
}
