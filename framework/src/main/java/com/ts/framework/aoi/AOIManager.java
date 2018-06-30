package com.ts.framework.aoi;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于十字链表实现的视野管理器
 * @author wl
 */
public class AOIManager<Entity extends AOIEntity> {
	private float view;
	private Map<Long, AOINode> nodeMap = new HashMap<>();
	private AOINode xListStart, yListStart;// 2个链表的首节点
	private AOINode xListEnd, yListEnd;// 2个链表的尾节点
	private AOIWatch<Entity> aoiWatch;

	public AOIManager(float view, AOIWatch<Entity> aoiWatch) {
		this.view = view;
		this.aoiWatch = aoiWatch;
	}

	/**
	 * 加入一个节点
	 */
	public void addNode(Entity enterEntity) {
		if (this.nodeMap.containsKey(enterEntity.getId())) {
			throw new RuntimeException("node " + enterEntity.getId() + " existed in AOIManager");
		}
		AOINode node = new AOINode(enterEntity);
		if (this.nodeMap.isEmpty()) {
			this.xListStart = (this.yListStart = this.xListEnd = this.yListEnd = node);
		} else {
			if (this.xListStart.getX() >= enterEntity.x) {
				this.xListStart.xPreNode = node;
				node.xNextNode = this.xListStart;
				this.xListStart = node;
			} else {
				AOINode currentNode = this.xListStart.xNextNode;
				while ((currentNode != null) && (currentNode.getX() <= enterEntity.x))
					currentNode = currentNode.xNextNode;

				if (currentNode != null) {
					currentNode.xPreNode.xNextNode = node;
					node.xPreNode = currentNode.xPreNode;
					node.xNextNode = currentNode;
					currentNode.xPreNode = node;
				} else {
					this.xListEnd.xNextNode = node;
					node.xPreNode = this.xListEnd;
					this.xListEnd = node;
				}
			}
			if (this.yListStart.getY() >= enterEntity.y) {
				this.yListStart.yPreNode = node;
				node.yNextNode = this.yListStart;
				this.yListStart = node;
			} else {
				AOINode currentNode = this.yListStart.yNextNode;
				while ((currentNode != null) && (currentNode.getY() <= enterEntity.y))
					currentNode = currentNode.yNextNode;

				if (currentNode != null) {
					currentNode.yPreNode.yNextNode = node;
					node.yPreNode = currentNode.yPreNode;
					node.yNextNode = currentNode;
					currentNode.yPreNode = node;
				} else {
					this.yListEnd.yNextNode = node;
					node.yPreNode = this.yListEnd;
					this.yListEnd = node;
				}
			}
		}
		this.nodeMap.put(enterEntity.getId(), node);

		// 更新视野列表
		watchEnter(findView(enterEntity), enterEntity);
	}

	/**
	 * 移除节点
	 */
	public Entity removeNode(long nodeId) {
		Entity entity = getEntity(nodeId);
		removeNode(entity);
		return entity;
	}

	/**
	 * 移除节点
	 */
	public void removeNode(Entity removeEntity) {
		if (removeEntity == null) {
			throw new NullPointerException("AOIEntity");
		}

		watchLeave(findView(removeEntity), removeEntity);

		AOINode node = this.nodeMap.remove(removeEntity.getId());
		if (node == null) {
			return;
		}
		if (this.nodeMap.isEmpty()) {
			this.xListStart = (this.yListStart = this.xListEnd = this.yListEnd = null);
			return;
		}

		unLink(node);

		removeEntity.clearView();
	}

	/**
	 * 移动节点到指定点
	 */
	public void moveNodeTo(long nodeId, float x, float y) {
		Entity entity = getEntity(nodeId);
		moveNodeTo(entity, x, y);
	}

	/**
	 * 移动节点到指定点
	 */
	public void moveNodeTo(Entity moveEntity, float x, float y) {
		if (moveEntity == null) {
			throw new NullPointerException("AOIEntity");
		}
		AOINode node = nodeMap.get(moveEntity.getId());
		if (node == null) {
			return;
		}
		if (xListStart == node && xListEnd == node) {// 只有一个点，无需视野变更
			node.moveTo(x, y);
			return;
		}

		// 原视野列表
		List<Entity> oldViewList = findView(moveEntity.getId());

		unLink(node);
		if (node.getX() <= x) {// 向右移动
			if (xListStart.getX() >= x) {// 替换首节点
				xListStart.xPreNode = node;
				node.xNextNode = xListStart;
				node.xPreNode = null;
				xListStart = node;
			} else {// 替换其他节点
				AOINode currentNode = node.xNextNode;// 当前节点
				while ( currentNode != null && currentNode.getX() < x) {
					currentNode = currentNode.xNextNode;
				}
				if (currentNode != null) {
					if (currentNode.xPreNode != null) {
						currentNode.xPreNode.xNextNode = node;
					}
					node.xPreNode = currentNode.xPreNode;
					currentNode.xPreNode = node;
					node.xNextNode = currentNode;
				} else {
					xListEnd.xNextNode = node;// 替换尾节点
					node.xPreNode = xListEnd;
					node.xNextNode = null;
					xListEnd = node;
				}
			}
		} else if (node.getX() > x) {// 向左移动
			if (xListEnd.getX() <= x) {// 替换尾节点
				xListEnd.xNextNode = node;
				node.xPreNode = xListEnd;
				node.xNextNode = null;
				xListEnd = node;
			} else {
				AOINode currentNode = node.xPreNode;// 当前节点
				while (currentNode != null && currentNode.getX() > x){
					currentNode = currentNode.xPreNode;
				}
				if (currentNode != null) {
					if (currentNode.xNextNode != null) {
						currentNode.xNextNode.xPreNode = node;
					}
					node.xNextNode = currentNode.xNextNode;
					currentNode.xNextNode = node;
					node.xPreNode = currentNode;
				} else {
					xListStart.xPreNode = node;// 替换首节点
					node.xNextNode = xListStart;
					node.xPreNode = null;
					xListStart = node;
				}
			}
		}
		if (node.getY() <= y) {// 向右移动
			if (yListStart.getY() >= y) {// 替换首节点
				yListStart.yPreNode = node;
				node.yNextNode = yListStart;
				node.yPreNode = null;
				yListStart = node;
			} else {
				AOINode currentNode = node.yNextNode;// 当前节点
				 while (currentNode != null && currentNode.getY() < y){
					 currentNode = currentNode.yNextNode;
				 }
				if (currentNode != null) {
					if (currentNode.yPreNode != null) {
						currentNode.yPreNode.yNextNode = node;
					}
					node.yPreNode = currentNode.yPreNode;
					currentNode.yPreNode = node;
					node.yNextNode = currentNode;
				} else {
					yListEnd.yNextNode = node;// 替换尾节点
					node.yPreNode = yListEnd;
					node.yNextNode = null;
					yListEnd = node;
				}
			}
		} else if (node.getY() > y) {// 向左移动
			if (yListEnd.getY() <= y) {// 替换尾节点
				yListEnd.yNextNode = node;
				node.yPreNode = yListEnd;
				node.yNextNode = null;
				yListEnd = node;
			} else {
				AOINode currentNode = node.yPreNode;// 当前节点
				while (currentNode != null && currentNode.getY() > y){
					currentNode = currentNode.yPreNode;
				}
				if (currentNode != null) {
					if (currentNode.yNextNode != null) {
						currentNode.yNextNode.yPreNode = node;
					}
					node.yNextNode = currentNode.yNextNode;
					currentNode.yNextNode = node;
					node.yPreNode = currentNode;
				} else {
					yListStart.yPreNode = node;// 替换首节点
					node.yNextNode = yListStart;
					node.yPreNode = null;
					yListStart = node;
				}
			}
		}
		node.moveTo(x, y);

		// 新视野列表
		List<Entity> newViewList = findView(moveEntity.getId());

		List<Entity> moveList = new LinkedList<>();
		// 检测视野变更
		Entity aoiEntity;
		Iterator<Entity> iterator = oldViewList.iterator();
		while (iterator.hasNext()) {
			aoiEntity = iterator.next();
			if (newViewList.contains(aoiEntity)) {// 老视野对象依然在新视野中，则为移动
				moveList.add(aoiEntity);// 加入移动列表
				newViewList.remove(aoiEntity);
				iterator.remove();
			}
		}

		watchLeave(oldViewList, moveEntity);// 老视野剩余单位为离开视野的单位
		watchEnter(newViewList, moveEntity);// 新视野剩余单位为进入视野的单位
		watchMove(moveList, moveEntity);// 存在于新老视野中的，为移动单位
	}

	/**
	 * 解除节点与其他节点的关系
	 */
	private void unLink(AOINode node) {
		if (xListStart == node) {// x首
			xListStart = node.xNextNode;
			xListStart.xPreNode = null;
		} else if (xListEnd == node) {// x尾
			xListEnd = node.xPreNode;
			xListEnd.xNextNode = null;
		} else {// 中间
			node.xPreNode.xNextNode = node.xNextNode;
			node.xNextNode.xPreNode = node.xPreNode;
		}
		if (yListStart == node) {
			yListStart = node.yNextNode;
			yListStart.yPreNode = null;
		} else if (yListEnd == node) {
			yListEnd = node.yPreNode;
			yListEnd.yNextNode = null;
		} else {
			node.yPreNode.yNextNode = node.yNextNode;
			node.yNextNode.yPreNode = node.yPreNode;
		}
	}

	/**
	 * 获取实体对象
	 */
	@SuppressWarnings("unchecked")
	public <T extends AOIEntity> T getEntity(long id) {
		AOINode aoiNode = nodeMap.get(id);
		return aoiNode == null ? null : (T) aoiNode.entity;
	}

	/**
	 * 返回所有节点（副本list）
	 */
	@SuppressWarnings("unchecked")
	public <T extends AOIEntity> List<T> getAll() {
		return nodeMap.values().stream()
				.map(node -> (T) node.entity)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public int size() {
		return nodeMap.size();
	}

	public LinkedList<Entity> findView(Entity entity) {
		if (entity == null) {
			throw new NullPointerException("AOIEntity");
		}
		return findView(entity.getId());
	}

	@SuppressWarnings("unchecked")
	public <T extends AOIEntity> LinkedList<T> findView(long entityId) {
		AOINode node = nodeMap.get(entityId);
		if (node == null) {
			return new LinkedList<>();
		}
		LinkedList<T> xViewList = new LinkedList<>();
		AOINode nextNode = node.xNextNode;
		AOINode preNode = node.xPreNode;
		// [x, x+view]
		// int num = 10;
		while (nextNode != null && nextNode.getX() - node.getX() < view) {
			xViewList.add((T) nextNode.entity);
			nextNode = nextNode.xNextNode;
			// if (num-- < 0) {
			// Thread.dumpStack();
			// return xViewList;
			// }
		}
		// [x, x-view]
		// num = 10;
		while (preNode != null && node.getX() - preNode.getX() < view) {
			xViewList.add((T) preNode.entity);
			preNode = preNode.xPreNode;
			// if (num-- < 0) {
			// Thread.dumpStack();
			// return xViewList;
			// }
		}

		LinkedList<T> yViewList = new LinkedList<>();
		nextNode = node.yNextNode;
		preNode = node.yPreNode;
		// [y, y+view]
		// num = 10;
		while (nextNode != null && nextNode.getY() - node.getY() < view) {
			yViewList.add((T) nextNode.entity);
			nextNode = nextNode.yNextNode;
			// if (num-- < 0) {
			// Thread.dumpStack();
			// return xViewList;
			// }
		}
		// [y, y-view]
		// num = 10;
		while (preNode != null && node.getY() - preNode.getY() < view) {
			yViewList.add((T) preNode.entity);
			preNode = preNode.yPreNode;
			// if (num-- < 0) {
			// Thread.dumpStack();
			// return xViewList;
			// }
		}

		xViewList.retainAll(yViewList);// 取交集
		return xViewList;
	}

	/**
	 * 触发进入视野
	 */
	private void watchEnter(List<Entity> watchers, Entity incomer) {
		for (AOIEntity watcher : watchers) {
			watcher.addEntity(incomer);
			incomer.addEntity(watcher);
		}
		aoiWatch.enter(watchers, incomer);
	}

	/**
	 * 触发离开视野
	 */
	private void watchLeave(List<Entity> watchers, Entity leaver) {
		for (AOIEntity watcher : watchers) {
			watcher.removeEntity(leaver.getId());
			leaver.removeEntity(watcher.getId());
		}
		aoiWatch.leave(watchers, leaver);
	}

	/**
	 * 触发视野内的移动
	 */
	private void watchMove(List<Entity> watchers, Entity mover) {
		aoiWatch.move(watchers, mover);
	}

}
