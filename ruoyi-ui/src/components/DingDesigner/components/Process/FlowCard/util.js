import nodeConfig from "./config.js";
const isEmpty = data => data === null || data === undefined || data === ''
const isEmptyArray = data => Array.isArray( data ) ? data.length === 0 : true

export class NodeUtils {

  /**
   * 根据自增数生成64进制id
   * @returns 64进制id字符串
   */
  static idGenerator () {
    let qutient = (new Date() - new Date('2020-08-01'))
    qutient += Math.ceil(Math.random() * 1000) // 防止重複
    const chars = '0123456789ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz';
    const charArr = chars.split( "" )
    const radix = chars.length;
    const res = []
    do {
      let mod = qutient % radix;
      qutient = ( qutient - mod ) / radix;
      res.push( charArr[mod] )
    } while ( qutient );
    return res.join( '' )
  }


  /**
   * 判断节点类型
   * @param {Node} node - 节点数据
   * @returns Boolean
   */
  static isConditionNode ( node ) {
    return node && node.type === 'condition'
  }
  static isConcurrentNode ( node ) {
    return node && node.type === 'concurrent'
  }
  static isDelayNode ( node ) {
    return node && node.type === 'delay'
  }
  static isTriggerNode ( node ) {
    return node && node.type === 'trigger'
  }
  static isCopyNode ( node ) {
    return node && node.type === 'copy'
  }
  static isStartNode ( node ) {
    return node && node.type === 'start'
  }
  static isApproverNode ( node ) {
    return node && node.type === 'approver'
  }
  /**
   * 创建指定节点
   * @param { String } type - 节点类型
   * @param { Object } previousNodeId - 父节点id
   * @returns { Object } 节点数据
   */
  static createNode ( type, previousNodeId ) {
    console.log("createNode type,previousNodeId",type,previousNodeId)
    let res = JSON.parse( JSON.stringify( nodeConfig[type] ) )
    console.log("createNode res",res)
    res.nodeId = this.idGenerator()
    res.prevId = previousNodeId
    return res
  }
  /**
   * 获取指定节点的父节点（前一个节点）
   * @param { String } prevId - 父节点id
   * @param { Object } processData - 流程图全部数据
   * @returns { Object } 父节点
   */
  static getPreviousNode ( prevId, processData ) {
    if ( processData.nodeId === prevId ) return processData
    if ( processData.childNode ) {
      let r1 = this.getPreviousNode( prevId, processData.childNode )
      if ( r1 ) {
        return r1
      }
    }
    if ( processData.conditionNodes ) {
      for ( let c of processData.conditionNodes ) {
        let r2 = this.getPreviousNode( prevId, c )
        if ( r2 ) {
          return r2
        }
      }
    }
	if ( processData.concurrentNodes ) {
	  for ( let c of processData.concurrentNodes ) {
	    let r2 = this.getPreviousNode( prevId, c )
	    if ( r2 ) {
	      return r2
	    }
	  }
	}
  }


  /**
   * 删除节点
   * @param { Object  } nodeData - 被删除节点的数据
   * @param { Object  } processData - 流程图的所有节点数据
   */
  static deleteNode ( nodeData, processData, checkEmpty = true ) {
    let prevNode = this.getPreviousNode( nodeData.prevId, processData )
    if ( checkEmpty && prevNode.type === 'empty' ) {
      if ( this.isConditionNode( nodeData )) {
        const willDelBranch = prevNode.conditionNodes.length === 2
        const target = willDelBranch ? prevNode : nodeData
        this.deleteNode( target, processData,  willDelBranch)
      } if ( this.isConcurrentNode( nodeData )) {
        const willDelBranch = prevNode.isConcurrentNode.length === 2
        const target = willDelBranch ? prevNode : nodeData
        this.deleteNode( target, processData,  willDelBranch)
      } else {
        if ( isEmptyArray( prevNode.conditionNodes ) ) {
          this.deleteNode( prevNode, processData )
        }
        if ( isEmptyArray( prevNode.concurrentNodes ) ) {
          this.deleteNode( prevNode, processData )
        }
        this.deleteNode( nodeData, processData, false )
      }
      // this.deleteNode( prevNode, processData )
      // !this.isConditionNode(nodeData) && this.deleteNode(nodeData, processData)
      return
    }

    //if ( this.isConditionNode( nodeData ) ) {
      let concatChild = ( prev, delNode ) => {
        prev.childNode = delNode.childNode
        isEmptyArray( prev.conditionNodes ) && ( prev.conditionNodes = delNode.conditionNodes )
        prev.childNode && ( prev.childNode.prevId = prev.nodeId )
        prev.conditionNodes && prev.conditionNodes.forEach( c => c.prevId = prev.nodeId );
      }
    //}
    /*if ( this.isConcurrentNode( nodeData ) ) {
      let concatChild = ( prev, delNode ) => {
        prev.childNode = delNode.childNode
        isEmptyArray( prev.concurrentNodes ) && ( prev.concurrentNodes = delNode.concurrentNodes )
        prev.childNode && ( prev.childNode.prevId = prev.nodeId )
        prev.concurrentNodes && prev.concurrentNodes.forEach( c => c.prevId = prev.nodeId );
      }
    }*/
    console.log("deleteNode concatChild",concatChild)
    if ( this.isConditionNode( nodeData ) ) {
      let cons = prevNode.conditionNodes
      let index = cons.findIndex( c => c.nodeId === nodeData.nodeId )
      if ( cons.length > 2 ) {
        cons.splice( index, 1 )
      } else {
        let anotherCon = cons[+( !index )]
        delete prevNode.conditionNodes
        if ( prevNode.childNode ) {
          let endNode = anotherCon
          while ( endNode.childNode ) {
            endNode = endNode.childNode
          }
          endNode.childNode = prevNode.childNode
          endNode.childNode.prevId = endNode.nodeId
        }
        concatChild( prevNode, anotherCon )
        if (prevNode.childNode && prevNode.childNode.type === 'empty') {
          this.deleteNode(prevNode.childNode, prevNode)
        }
      }
      // 重新编排优先级
      cons.forEach( ( c, i ) => c.properties.priority = i )
      return
    }
    if ( this.isConcurrentNode( nodeData ) ) {
      let cons = prevNode.concurrentNodes
      let index = cons.findIndex( c => c.nodeId === nodeData.nodeId )
      if ( cons.length > 2 ) {
        cons.splice( index, 1 )
      } else {
        let anotherCon = cons[+( !index )]
        delete prevNode.concurrentNodes
        if ( prevNode.childNode ) {
          let endNode = anotherCon
          while ( endNode.childNode ) {
            endNode = endNode.childNode
          }
          endNode.childNode = prevNode.childNode
          endNode.childNode.prevId = endNode.nodeId
        }
        concatChild( prevNode, anotherCon )
        if (prevNode.childNode && prevNode.childNode.type === 'empty') {
          this.deleteNode(prevNode.childNode, prevNode)
        }
      }
      // 重新编排优先级
      cons.forEach( ( c, i ) => c.properties.priority = i )
      return
    }
    concatChild( prevNode, nodeData )
  }
  // TODO:
  // static copyNode ( nodeData, processData ) {
  //   let prevNode = this.getPreviousNode( nodeData.prevId, processData )
  //   let index = prevNode.conditionNodes.findIndex( c => c.nodeId === nodeData.nodeId )

  // }
  /**
   * 添加审批节点（普通节点 approver）
   * @param { Object } data - 目标节点数据，在该数据节点之后添加审计节点
   * @param { Object } isBranchAction - 目标节点数据，是否是条件分支
   * @param { Object } newChildNode - 传入的新的节点 用户操作均为空  删除操作/添加抄送人 会传入该参数 以模拟添加节点
   */
  static addApprovalNode ( data, isBranchAction, newChildNode = undefined ) {
    console.log("addApprovalNode data,isBranchAction",data,isBranchAction)
    let oldChildNode = data.childNode;
    newChildNode = newChildNode || this.createNode( "approver", data.nodeId )
    console.log("addApprovalNode newChildNode",newChildNode)
    data.childNode = newChildNode
    if ( oldChildNode ) {
      newChildNode.childNode = oldChildNode
      oldChildNode.prevId = newChildNode.nodeId
    }
    let conditionNodes = data.conditionNodes
    if ( Array.isArray( conditionNodes ) && !isBranchAction && conditionNodes.length ) {
      newChildNode.conditionNodes = conditionNodes.map( c => {
        c.prevId = newChildNode.nodeId
        return c
      } )
      delete data.conditionNodes
    }
    let concurrentNodes = data.concurrentNodes
    if ( Array.isArray( concurrentNodes ) && !isBranchAction && concurrentNodes.length ) {
      newChildNode.concurrentNodes = concurrentNodes.map( c => {
        c.prevId = newChildNode.nodeId
        return c
      } )
      delete data.concurrentNodes
    }
    if ( oldChildNode && oldChildNode.type === 'empty' && newChildNode.type !== 'empty' && oldChildNode.conditionNodes.length === 0 ) {
      this.deleteNode( oldChildNode, data )
    }
  }
  /**
   * 添加空节点
   * @param { Object } data - 空节点的父级节点
   * @return { Object } emptyNode - 空节点数据
   */
  static addEmptyNode ( data ) {
    let emptyNode = this.createNode( 'empty', data.nodeId )
    this.addApprovalNode( data, true, emptyNode )
    return emptyNode
  }

  static addCopyNode ( data, isBranchAction ) {
    // 复用addApprovalNode  因为抄送人和审批人基本一致
    this.addApprovalNode( data, isBranchAction, this.createNode( 'copy', data.nodeId ) )
  }

  static addDelayNode ( data, isBranchAction ) {
    // 复用addApprovalNode  因为抄送人和审批人基本一致
    this.addApprovalNode( data, isBranchAction, this.createNode( 'delay', data.nodeId ) )
  }

  static addTriggerNode ( data, isBranchAction ) {
    // 复用addApprovalNode  因为抄送人和审批人基本一致
    this.addApprovalNode( data, isBranchAction, this.createNode( 'trigger', data.nodeId ) )
  }

  /**
   * 添加条件节点 condition 通过点击添加条件进入该操作
   * @param { Object } data - 目标节点所在分支数据，在该分支最后添加条件节点
   */
  static appendConditionNode ( data ) {
    console.log("appendConditionNode data",data)
    const conditions = data.conditionNodes
    let node = this.createNode( 'condition', data.nodeId )
    let defaultNodeIndex = conditions.findIndex( node => node.properties.isDefault )
    node.properties.priority = conditions.length
    if ( defaultNodeIndex > -1 ) {
      conditions.splice( -1, 0, node ) // 插在倒数第二个
      //更新优先级
      node.properties.priority = conditions.length - 2
      conditions[conditions.length - 1].properties.priority = conditions.length - 1
    } else {
      conditions.push( node )
    }
    //this.setDefaultCondition( node, data )
  }
  /**
   * 添加条件分支 branch
   * @param { Object } data - 目标节点所在节点数据，在该节点最后添加分支节点
   */
  static appendBranch ( data, isBottomBtnOfBranch ) {
    // isBottomBtnOfBranch 用户点击的是分支树下面的按钮
    let nodeData = data
    // 由于conditionNodes是数组 不能添加下级分支 故在两个分支树之间添加一个不会显示的正常节点 兼容此种情况
    if ( Array.isArray( data.conditionNodes ) && data.conditionNodes.length ) {
      if ( isBottomBtnOfBranch ) {
        // 添加一个模拟用的空白节点并返回这个节点，作为新分支的父节点
        nodeData = this.addEmptyNode( nodeData, true )
      } else {
        let emptyNode = this.addEmptyNode( nodeData, true )
        emptyNode.conditionNodes = nodeData.conditionNodes
        emptyNode.conditionNodes.forEach( n => {
          n.prevId = emptyNode.nodeId
        } )
      }
    }
    let conditionNodes = [
      this.createNode( "condition", nodeData.nodeId ),
      this.createNode( "condition", nodeData.nodeId )
    ].map( ( c, i ) => {
      c.properties.title += i + 1;
      c.properties.priority = i;
      return c
    } )
    nodeData.conditionNodes = conditionNodes
  }

  /**
   * 添加并行节点 concurrent 通过点击添加并行进入该操作
   * @param { Object } data - 目标节点所在分支数据，在该分支最后添加并行节点
   */
  static addConcurrentNode ( data ) {
    console.log("addConcurrentNode data",data)
    const concurrents = data.concurrentNodes
    let node = this.createNode( 'concurrent', data.nodeId )
    let defaultNodeIndex = concurrents.findIndex( node => node.properties.isDefault )
    if ( defaultNodeIndex > -1 ) {
      concurrents.splice( -1, 0, node ) // 插在倒数第二个
    } else {
      concurrents.push( node )
    }
    this.setDefaultConcurrent( node, data )
  }
  /**
   * 添加并行分支 branch
   * @param { Object } data - 目标节点所在节点数据，在该节点最后添加分支节点
   */
  static addConcurrentBranch ( data, isBottomBtnOfBranch ) {
    //console.log("addConcurrentBranch,data, isBottomBtnOfBranch",data,isBottomBtnOfBranch)
    // isBottomBtnOfBranch 用户点击的是分支树下面的按钮
    let nodeData = data
    // 由于concurrentNodes是数组 不能添加下级分支 故在两个分支树之间添加一个不会显示的正常节点 兼容此种情况
    if ( Array.isArray( data.concurrentNodes ) && data.concurrentNodes.length ) {
      if ( isBottomBtnOfBranch ) {
        // 添加一个模拟用的空白节点并返回这个节点，作为新分支的父节点
        nodeData = this.addEmptyNode( nodeData, true )
      } else {
        let emptyNode = this.addEmptyNode( nodeData, true )
        emptyNode.concurrentNodes = nodeData.concurrentNodes
        emptyNode.concurrentNodes.forEach( n => {
          n.prevId = emptyNode.nodeId
        } )
      }
    }
    let concurrentNodes = [
      this.createNode( "concurrent", nodeData.nodeId ),
      this.createNode( "concurrent", nodeData.nodeId )
    ].map( ( c, i ) => {
      c.properties.title += i + 1;
      return c
    } )
    nodeData.concurrentNodes = concurrentNodes
  }

  /**
   * 重设节点优先级（条件节点）
   * @param {Node} cnode - 当前节点
   * @param {Number} oldPriority - 替换前的优先级（在数组中的顺序）
   * @param {Node} processData - 整个流程图节点数据
   */
  static resortPrioByCNode ( cnode, oldPriority, processData ) {
    // 当前节点为默认节点 取消修改优先级
    console.log("resortPrioByCNode processData",processData)
    if ( cnode.properties.isDefault ) {
      cnode.properties.priority = oldPriority
      return
    }
    let prevNode = this.getPreviousNode( cnode.prevId, processData )
    let newPriority = cnode.properties.priority
    // 替换节点为默认节点 取消修改优先级
    if ( prevNode.conditionNodes[newPriority].properties.isDefault ) {
      cnode.properties.priority = oldPriority
      return
    }
    let delNode = prevNode.conditionNodes.splice( newPriority, 1, cnode )[0]
    delNode.properties.priority = oldPriority
    prevNode.conditionNodes[oldPriority] = delNode
  }

  /**
   * 提升条件节点优先级——排序在前
   * @param { Object } data - 目标节点数据
   * @param { Object  } processData - 流程图的所有节点数据
   */
  static increasePriority ( data, processData ) {
    if ( data.properties.isDefault ) {  // 默认节点不能修改优先级
      return
    }
    // 分支节点数据 包含该分支所有的条件节点
    let prevNode = this.getPreviousNode( data.prevId, processData )
    let branchData = prevNode.conditionNodes
    let index = branchData.findIndex( c => c === data )
    if ( index ) {
      // 和前一个数组项交换位置 Array.prototype.splice会返回包含被删除的项的集合（数组）
      branchData[index - 1].properties.priority = index
      branchData[index].properties.priority = index - 1
      branchData[index - 1] = branchData.splice( index, 1, branchData[index - 1] )[0]
    }
  }
  /**
   * 降低条件节点优先级——排序在后
   * @param { Object } data - 目标节点数据
   * @param { Object  } processData - 流程图的所有节点数据
   */
  static decreasePriority ( data, processData ) {
    // 分支节点数据 包含该分支所有的条件节点
    let prevNode = this.getPreviousNode( data.prevId, processData )
    let branchData = prevNode.conditionNodes
    let index = branchData.findIndex( c => c.nodeId === data.nodeId )
    if ( index < branchData.length - 1 ) {
      let lastNode = branchData[index + 1]
      if ( lastNode.properties.isDefault ) {  // 默认节点不能修改优先级
        return
      }
      // 和后一个数组项交换位置 Array.prototype.splice会返回包含被删除的项的集合（数组）
      lastNode.properties.priority = index
      branchData[index].properties.priority = index + 1
      branchData[index + 1] = branchData.splice( index, 1, branchData[index + 1] )[0]
    }
  }
  /**
 * 当有其他条件节点设置条件后 检查并设置最后一个节点为默认节点
 * @param {Node} cnode  - 当前节点
 * @param {Node} processData - 整个流程图节点数据或父级节点数据
 */
  static setDefaultCondition ( cnode, processData ) {
    const DEFAULT_TEXT = '其他情况进入此流程'
    const conditions = this.getPreviousNode( cnode.prevId, processData ).conditionNodes
    const hasCondition = node => node.properties && ( node.properties.initiator || !isEmptyArray( node.properties.conditions ) )
    const clearDefault = node => {
      node.properties.isDefault = false
      node.content === DEFAULT_TEXT && ( node.content = '请设置条件表达式' )
    }
    const setDefault = node => {
      node.properties.isDefault = true
      node.content = DEFAULT_TEXT
    }
    let count = 0
    conditions.slice( 0, -1 ).forEach( node => {
      hasCondition( node ) && count++
      clearDefault( node )
    } )
    const lastNode = conditions[conditions.length - 1]
    count > 0 && !hasCondition( lastNode ) ? setDefault( lastNode ) : clearDefault( lastNode )
  }

  /**
  * 当有其他并发节点设置并发后 检查并设置最后一个节点为默认节点
  * @param {Node} cnode  - 当前节点
  * @param {Node} processData - 整个流程图节点数据或父级节点数据
  */
   static setDefaultConcurrent ( cnode, processData ) {
     console.log("setDefaultConcurrent cnode",cnode)
     console.log("setDefaultConcurrent processData",processData)
     const DEFAULT_TEXT = '其他情况进入此流程'
     const concurrents = this.getPreviousNode( cnode.prevId, processData ).concurrentNodes
     const hasConcurrent = node => node.properties && ( node.properties.initiator || !isEmptyArray( node.properties.concurrents ) )
     const clearDefault = node => {
       node.properties.isDefault = false
       node.content === DEFAULT_TEXT && ( node.content = '请设置条件表达式' )
     }
     const setDefault = node => {
       node.properties.isDefault = true
       node.content = DEFAULT_TEXT
     }
     let count = 0
     concurrents.slice( 0, -1 ).forEach( node => {
       hasConcurrent( node ) && count++
       clearDefault( node )
     } )
     const lastNode = concurrents[concurrents.length - 1]
     count > 0 && !hasConcurrent( lastNode ) ? setDefault( lastNode ) : clearDefault( lastNode )
   }

  /**
   * 校验单个节点必填项完整性
   * @param {Node} node - 节点数据
   */
  static checkNode ( node, parent ) {
    // 抄送人应该可以默认自选
    let valid = true
    const props = node.properties
    this.isStartNode( node )
      && !props.initiator
      && ( valid = false )

    this.isConditionNode( node )
      && !props.isDefault
      && !props.initiator
      && isEmptyArray( props.conditions )
      && !this.checkChildNode ( node, parent )
      && ( valid = false )


    //this.isConcurrentNode( node )
     // && !this.checkChildNode ( node, parent )
     // && ( valid = false )

    const customSettings = ['myself', 'optional', 'director']
    this.isApproverNode( node )
      && !customSettings.includes( props.assigneeType )
      && isEmptyArray( props.approvers )
      && ( valid = false )


    return valid
  }

  /**
   * 校验子节点必填项完整性，目前主要是针对条件节点与并发节点
   * @param {Node} node - 节点数据
   */
  static checkChildNode ( node, parent ) {
    let valid = false
    if ( this.isConditionNode( node ) ) {
         if (!isEmpty(node.childNode)) {
           valid = true
         }
    }
    if ( this.isConcurrentNode( node ) ) {
        if (!isEmpty(node.childNode)) {
          valid = true
        }
    }
    return valid
  }

  /**
   * 校验延时子节点必填项完整性
   * @param {Node} node - 节点数据
   */
  static checkDelayNode ( node, parent ) {
    let valid = true
    if (node.properties.type === "AUTO") {
      if ((node.properties.dateTime || "") === ""){
        valid = false
      }
    } else {
      if (node.properties.type === "FIXED" && node.properties.time <= 0) {
        valid = false
      }
    }
    return valid
  }

  /**
   * 判断所有节点是否信息完整
   * @param {Node} processData - 整个流程图数据
   * @returns {Boolean}
   */
  static checkAllNode ( processData ) {
    let valid = true
    const loop = ( node, callback, parent ) => {

      !this.checkNode( node, parent ) && callback()
      if ( node.childNode ) loop( node.childNode, callback, parent )
      if ( !isEmptyArray( node.conditionNodes ) ) {
        node.conditionNodes.forEach( n => loop( n, callback, node ) )
      }
      if ( !isEmptyArray( node.concurrentNodes ) ) {
        console.log("node.concurrentNodes ", node.concurrentNodes)
        node.concurrentNodes.forEach( n => loop( n, callback, node ) )
      }
    }
    loop( processData, () => valid = false )
    return valid
  }
}

/**
 * 添模拟数据
 */
export function getMockData () {
  let startNode = NodeUtils.createNode( "start" );
  startNode.childNode = NodeUtils.createNode( "approver", startNode.nodeId )
  return startNode;
}

