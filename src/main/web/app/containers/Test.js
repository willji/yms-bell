/**
 * Created by xuemingli on 16/9/12.
 */
import React from 'react';
import { connect } from 'react-redux';
import * as Notification from '../actions/notification'

@connect(state => state, {notify: Notification.add})
export default class Test extends React.Component {
  constructor(props) {
    super(props);
    this.props.notify(Notification.INFO, "test");
  }

  render() {
    return <div>test</div>
  }
}
