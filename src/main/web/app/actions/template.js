/**
 * Created by xuemingli on 2016/10/13.
 */
import { PAYLOAD } from '../middleware/api';
import * as utils  from './utils';


export const LIST_ALL_TEMPLATES_TYPE_SET = utils.createTypes('list_all_templates');

export function list(page = 1, size = 50) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: LIST_ALL_TEMPLATES_TYPE_SET,
      endpoint: '/v1/template',
      params: {page, size}
    }
  })
}


export const CREATE_TEMPLATE_TYPE_SET = utils.createTypes('create_template');

export function create(template) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: CREATE_TEMPLATE_TYPE_SET,
      endpoint: '/v1/template',
      options: {
        method: 'POST',
        body: JSON.stringify(template)
      }
    }
  })
}


export const GET_TEMPLATE_INFO_TYPE_SET = utils.createTypes('get_template_info');

export function get(id) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: GET_TEMPLATE_INFO_TYPE_SET,
      endpoint: `/v1/template/${id}`
    }
  })
}

export const UPDATE_TEMPLATE_TYPE_SET = utils.createTypes('update_template');

export function update(id, template) {
  template.id = id;
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: UPDATE_TEMPLATE_TYPE_SET,
      endpoint: `/v1/template/${id}`,
      options: {
        method: 'PUT',
        body: JSON.stringify(template)
      }
    }
  })
}

export const DELETE_TEMPLATE_TYPE_SET = utils.createTypes('delete_template');

export function remove(id) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: DELETE_TEMPLATE_TYPE_SET,
      endpoint: `/v1/template/${id}`,
      options: {method: 'DELETE'}
    }
  })
}

export const SEARCH_TEMPLATE_TYPE_SET = utils.createTypes("search_template");

export function search(q) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: SEARCH_TEMPLATE_TYPE_SET,
      endpoint: '/v1/template/search',
      params: {q}
    }
  });
}

export const GET_TEMPLATE_VARS_TYPE_SET = utils.createTypes('get_template_vars');

export function getVars(name) {
  return dispatch => dispatch({
    [PAYLOAD]: {
      types: GET_TEMPLATE_VARS_TYPE_SET,
      endpoint: `/v1/template/vars`,
      params: {name}
    }
  })
}
