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
import Select from 'antd/lib/select';
import notification from 'antd/lib/notification';
import * as constants from '../constants';
import * as ItemAction from '../actions/item';

const Option = Select.Option;

@connect(state => ({
  result: state.itemSearchResult,
}), {
  search: ItemAction.search
})
export default class ItemSearch extends React.Component {
  constructor(props) {
    super(props);
    props.search('');
    this.state = {result: [], value: '', focus: null, loading: false}
  }

  @autobind
  handleBlur(e) {
    this.setState({
      focus: e.target === document.activeElement,
    });
  }

  @autobind
  handleFocus(e) {
    this.setState({
      focus: e.target === document.activeElement,
      value: ''
    });
  }

  @autobind
  handleSearch(value) {
    this.props.search(value);
    this.setState({value});
  }

  @autobind
  handleSelect(id) {
    this.props.onSelect(id)
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
    const options = this.state.result.map(it => <Option key={it.id} value={it.name} itemId={it.id}>{it.name}</Option>);

    return (
      <div>
        <Spin spinning={this.state.loading}>
          <div className="ant-search-input-wrapper" style={{marginTop: 8, marginBottom: 8}}>
            <Input.Group className={searchCls}>
              <Select combobox
                      placeholder="输入监控项名称搜索"
                      value={this.state.value}
                      defaultActiveFirstOption={false}
                      showArrow={false}
                      filterOption={false}
                      onSelect={(value, option) => this.handleSelect(option.props.itemId)}
                      onChange={this.handleSearch}
                      onFocus={this.handleFocus}
                      onBlur={this.handleBlur}>
                {options}
              </Select>
              <div className="ant-input-group-wrap">
                <Button icon="search" className={btnCls} onClick={this.handleSearch} />
              </div>
            </Input.Group>
          </div>
        </Spin>
      </div>
    )
  }
}
