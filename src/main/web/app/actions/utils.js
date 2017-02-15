/**
 * Created by xuemingli on 16/5/25.
 */

export function createTypes(name) {
  return {
    REQUEST: `${name}@REQUEST`,
    SUCCESS: `${name}@SUCCESS`,
    FAILURE: `${name}@FAILURE`
  }
}
