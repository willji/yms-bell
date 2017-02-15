import Guid from 'guid';
import * as constants from '../constants'

const initialState = {
  error: null,
  status: null,
  res: null,
  type: null,
  id: Guid.EMPTY
};

function makeTypes(actionTypes) {
  let requests = [];
  let successes = [];
  let failures = [];
  for (let actionType of actionTypes) {
    requests.push(actionType.REQUEST);
    successes.push(actionType.SUCCESS);
    failures.push(actionType.FAILURE);
  }
  return {requests, successes, failures};
}

export function createReduce(...actionTypes) {
  const {requests, successes, failures} = makeTypes(actionTypes);
  return (state = initialState, action = {}) => {
    switch (true) {
      case (requests.indexOf(action.type) >= 0):
        return Object.assign({}, state, {
          status: constants.STATUS_REQUEST,
          type: action.type,
          id: Guid.raw()
        });
      case (successes.indexOf(action.type) >= 0):
        return Object.assign({}, state, {
          status: constants.STATUS_SUCCESS,
          res: action.res,
          type: action.type,
          id: Guid.raw()
        });
      case (failures.indexOf(action.type) >= 0):
        return Object.assign({}, state, {
          status: constants.STATUS_FAILURE,
          error: action.error,
          type: action.type,
          id: Guid.raw()
        });
      default:
        return state;
    }
  };
}
