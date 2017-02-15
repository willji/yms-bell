/**
 * Created by xuemingli on 2016/10/18.
 */
import React from 'react'
import { connect } from 'react-redux'
import autobind from 'core-decorators/lib/autobind';
import classNames from 'classname';
import Spin from 'antd/lib/spin';
import Button from 'antd/lib/button';
import Input from 'antd/lib/input';
import Card from 'antd/lib/card';
import notification from 'antd/lib/notification';
import * as constants from '../constants';
import * as AppAction from '../actions/app';
import {set as navigate} from '../actions/navigation';

@connect(state => ({
  result: state.appSearchResult,
}), {
  navigate,
  search: AppAction.searchApp
})
export default class ApplicationSearch extends React.Component {
  constructor(props) {
    super(props);
    this.props.navigate('app', [{name: '应用搜索'}]);
    props.search('', -1);
    this.state = {result: [], value: '', focus: null, loading: false}
  }

  @autobind
  handleInputChange(e) {
    this.setState({
      value: e.target.value,
    });
  }

  @autobind
  handleFocusBlur(e) {
    this.setState({
      focus: e.target === document.activeElement,
    });
  }

  @autobind
  handleSearch() {
    this.props.search(this.state.value, -1)
  }

  componentWillReceiveProps(props) {
    if (props.result.id !== this.props.result.id) {
      switch (props.result.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case  constants.STATUS_FAILURE:
          notification.error({message: "错误", description: props.result.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.setState({loading: false, result: props.result.res});
          break;
      }
    }
    this.props = props;
  }

  render() {
    const btnCls = classNames({
      'ant-search-btn': true,
      'ant-search-btn-noempty': !!this.state.value.trim(),
    });
    const searchCls = classNames({
      'ant-search-input': true,
      'ant-search-input-focus': this.state.focus,
    });

    return (
      <div>
        <Spin spinning={this.state.loading}>
          <div className="ant-search-input-wrapper" style={{marginTop: 8, marginBottom: 8}}>
            <Input.Group className={searchCls}>
              <Input placeholder="输入全部或部分应用名搜索"
                     value={this.state.value}
                     onChange={this.handleInputChange}
                     onFocus={this.handleFocusBlur}
                     onBlur={this.handleFocusBlur}
                     onPressEnter={this.handleSearch}/>
              <div className="ant-input-group-wrap">
                <Button icon="search" className={btnCls} onClick={this.handleSearch} />
              </div>
            </Input.Group>
          </div>
          <div>
            {this.state.result.map(it => <div key={it}><a href={`${constants.URL_PREFIX}/app/${it}`}><h3>{it}</h3></a></div>)}
          </div>
        </Spin>
      </div>
    )
  }
}
