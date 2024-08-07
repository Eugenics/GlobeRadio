package com.eugenics.media_service.media3.data

import androidx.media3.common.MediaItem

data class MediaTree(
    val nodes: MutableMap<String, MediaTreeNode>
) {
    fun addNode(node: MediaTreeNode): MediaTreeNode {
        this.nodes[node.id] = node
        return node
    }

    companion object {
        const val ROOT_ID = "ROOT_ID"
    }
}

data class MediaTreeNode(
    val id: String,
    val item: MediaItem,
    val children: MutableList<MediaItem>
) {
    fun addChild(item: MediaItem) {
        this.children.add(item)
    }

    fun getChild(childId: String) = this.children.find { it.mediaId == childId }

    companion object {
        fun generate(
            id: String,
            item: MediaItem,
            children: List<MediaItem> = listOf()
        ): MediaTreeNode =
            MediaTreeNode(id = id, item = item, children = children.toMutableList())
    }
}
