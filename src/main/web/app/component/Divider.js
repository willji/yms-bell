/**
 * Created by xuemingli on 2016/10/17.
 */
import React from 'react';

import styles from './divider.post.css';

export default class Divider extends React.Component {
  static propTypes = {
    content: React.PropTypes.string
  };

  render() {
    return <div className={styles.divider}>{this.props.content}</div>
  }
}
