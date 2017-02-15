/* eslint-disable */
export const DEFAULT_HEADERS = {
  ['Accept']: 'application/json',
  ['Content-Type']: 'application/json',
  ['X-Service']: 'yms-bell'
};
/* eslint-enable */


export const STATUS_REQUEST = Symbol('STATUS_REQUEST');
export const STATUS_SUCCESS = Symbol('STATUS_SUCCESS');
export const STATUS_FAILURE = Symbol('STATUS_FAILURE');

export const CONTEXT_PATH = '';
export const URL_PREFIX = `${CONTEXT_PATH}/ui`;

export const NAVIGATION_ITEMS = {
  root: {prefix: '', url: ''}
};

export const SCRIPT_TEMPLATE = `import com.ymatou.op.yms.bell.template.*

class Task extends BaseTask {
    void setup() {
      
    }
    
    void run() {
    }
}`;
