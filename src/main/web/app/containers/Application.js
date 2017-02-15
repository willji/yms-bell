/**
 * Created by xuemingli on 2016/11/1.
 */
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
import Collapse from 'antd/lib/collapse';
import Popconfirm from 'antd/lib/popconfirm';
import notification from 'antd/lib/notification';
import * as constants from '../constants';
import * as AppAction from '../actions/app';
import * as ItemAction from '../actions/item';
import {set as navigate} from '../actions/navigation';
import ItemSearch from './ItemSearch';

const Panel = Collapse.Panel;

@connect(state => ({
  api: state.api,
  result: state.apiSearchResult,
  items: state.itemSearchResult
}), {
  navigate,
  search: AppAction.searchApi,
  get: AppAction.getApi,
  searchItem: ItemAction.search,
  add: AppAction.addItem,
  remove: AppAction.removeItem
})
export default class Application extends React.Component {
  constructor(props) {
    super(props);
    this.app = this.props.params.app;
    this.props.navigate('app', [{name: '应用', url: '/apps'}, {name: this.app}]);
    props.search('', this.app, -1);
    this.state = {result: [], value: '', focus: null, loading: false, items: []}
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
    this.props.search(this.state.value, this.app, -1)
  }

  @autobind
  handleAddItem(name, item) {
    this.props.add(name, this.app, item)
  }

  @autobind
  handleRemoveItem(name, item) {
    this.props.remove(name, this.app, item);
  }

  @autobind
  handleChange(key) {
    if (key) {
      this.props.get(key, this.app)
    }
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

    if (props.api.id !== this.props.api.id) {
      switch (props.api.status) {
        case constants.STATUS_REQUEST:
          this.setState({loading: true});
          break;
        case  constants.STATUS_FAILURE:
          notification.error({message: "错误", description: props.api.error});
          this.setState({loading: false});
          break;
        case constants.STATUS_SUCCESS:
          this.setState({loading: false, items: props.api.res.items});
          break;
      }
    }
    this.props = props;
  }

  renderApi(name) {
    return (
      <Panel header={name} key={name}>
        <Spin spinning={this.state.loading}>
          <ItemSearch onSelect={id => this.handleAddItem(name, id)}/>
          {
            this.state.items.map(it => (
              <Button.Group key={it.id} style={{width: '100%', display: 'flex', marginTop: 8}}>
                <Button type="ghost" style={{flex: 63}}><a href={`${constants.URL_PREFIX}/item/${it.id}`}>{it.name}</a></Button>
                <Popconfirm title="确定删除此监控项吗？"
                            onConfirm={() => this.handleRemoveItem(name, it.id)}
                            okText="删除" cancelText="撤销">
                  <Button icon="delete" style={{flex: 1}}/>
                </Popconfirm>
              </Button.Group>
            ))
          }
        </Spin>
      </Panel>
    )
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
              <Input placeholder="输入全部或部分api名称搜索"
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
            <Collapse accordion onChange={this.handleChange}>
              {this.state.result.map(it => this.renderApi(it))}
            </Collapse>
          </div>
        </Spin>
      </div>
    )
  }
}
