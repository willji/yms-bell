/**
 * Created by xuemingli on 16/9/13.
 */
import { PAYLOAD } from '../middleware/api';
import * as utils  from './utils';


export const LIST_ALL_ITEMS_TYPE_SET = utils.createTypes('list_all_items');

export function list(page = 1, size = 50) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: LIST_ALL_ITEMS_TYPE_SET,
      endpoint: '/v1/item',
      params: {page, size}
    }
  })
}


export const CREATE_ITEM_TYPE_SET = utils.createTypes('create_item');

export function create(name, cron, template,  variables = {}, descriptor = null) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: CREATE_ITEM_TYPE_SET,
      endpoint: '/v1/item',
      options: {
        method: 'POST',
        body: JSON.stringify({name, cron, variables, descriptor, templateName:template})
      }
    }
  })
}

export const SUBSCRIBE_ITEM_TYPE_SET = utils.createTypes('subscribe_item');

export function subscribe(id) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: SUBSCRIBE_ITEM_TYPE_SET,
      endpoint: `/v1/item/${id}/subscribe`,
      options: {method: 'PUT'}
    }
  })
}


export const UNSUBSCRIBE_ITEM_TYPE_SET = utils.createTypes('unsubscribe_item');

export function unsubscribe(id) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: UNSUBSCRIBE_ITEM_TYPE_SET,
      endpoint: `/v1/item/${id}/unsubscribe`,
      options: {method: 'PUT'}
    }
  })
}


export const ENABLE_ITEM_TYPE_SET = utils.createTypes('enable_item');

export function enable(id) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: ENABLE_ITEM_TYPE_SET,
      endpoint: `/v1/item/${id}/enable`,
      options: {method: 'PUT'}
    }
  })
}

export const DISABLE_ITEM_TYPE_SET = utils.createTypes('disable_item');

export function disable(id) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: ENABLE_ITEM_TYPE_SET,
      endpoint: `/v1/item/${id}/disable`,
      options: {method: 'PUT'}
    }
  })
}


export const GET_ITEM_INFO_TYPE_SET = utils.createTypes('get_item_info');

export function get(id) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: GET_ITEM_INFO_TYPE_SET,
      endpoint: `/v1/item/${id}`
    }
  })
}

export const UPDATE_ITEM_TYPE_SET = utils.createTypes('update_item');

export function update(id, name, cron, template, enable = true, variables = {}, descriptor = null) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: UPDATE_ITEM_TYPE_SET,
      endpoint: `/v1/item/${id}`,
      options: {
        method: 'PUT',
        body: JSON.stringify({name, cron, variables, descriptor, templateName:template, enable})
      }
    }
  })
}

export const EXECUTE_ITEM_TYPE_SET = utils.createTypes("execute_item");

export function execute(id) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: EXECUTE_ITEM_TYPE_SET,
      endpoint: `/v1/item/${id}/execute`
    }
  })
}


export const ADD_APP_TYPE_SET = utils.createTypes('item_add_app');

export function addApp(id, name) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: ADD_APP_TYPE_SET,
      endpoint: `/v1/item/${id}/app`,
      params: {name},
      options: {method: 'PUT'}
    }
  })
}

export const REMOVE_APP_TYPE_SET = utils.createTypes("item_remove_app");

export function removeApp(id, name) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: REMOVE_APP_TYPE_SET,
      endpoint: `/v1/item/${id}/app`,
      params: {name},
      options: {method: 'DELETE'}
    }
  })
}

export const ADD_API_TYPE_SET = utils.createTypes("item_add_apiset");

export function addApi(id, name, api) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: ADD_API_TYPE_SET,
      endpoint: `/v1/item/${id}/api`,
      params: {app: name, api: api},
      options: {method: 'PUT'}
    }
  })
}

export const REMOVE_API_TYPE_SET = utils.createTypes("item_remove_apiset");

export function removeApi(id, name, api) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: REMOVE_API_TYPE_SET,
      endpoint: `/v1/item/${id}/api`,
      params: {app:name, api},
      options: {method: 'DELETE'}
    }
  })
}


export const GET_ITEM_APPS_TYPE_SET = utils.createTypes('item_get_apps');

export function getApps(id, page = 1, size = 20) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: GET_ITEM_APPS_TYPE_SET,
      endpoint: `/v1/item/${id}/apps`,
      params: {page, size}
    }
  })
}


export const GET_ITEM_APIS_TYPE_SET = utils.createTypes("item_get_apis");

export function getApis(id, app, page = 1, size = 50) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: GET_ITEM_APIS_TYPE_SET,
      endpoint: `/v1/item/${id}/apis`,
      params: {id, app, page, size}
    }
  })
}


export const GET_ITEM_VARS_TYPE_SET = utils.createTypes('item_get_vars');

export function getVars(id) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: GET_ITEM_VARS_TYPE_SET,
      endpoint: `/v1/item/${id}/vars`
    }
  })
}

export const SEARCH_ITEM_TYPE_SET = utils.createTypes('item_search');
export function search(q) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: SEARCH_ITEM_TYPE_SET,
      endpoint: '/v1/item/search',
      params: {q}
    }
  })
}
