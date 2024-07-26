package com.google.ai.sample.models

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

@JacksonXmlRootElement(localName = "rss")
data class RssFeed(
    @JacksonXmlProperty(localName = "channel")
    var channel: Channell? = null,

    @JacksonXmlProperty(localName = "version", isAttribute = true)
    var version: String? = null
)

@JacksonXmlRootElement(localName = "channel")
data class Channell(

    @JacksonXmlProperty(localName = "title")
    var title: String? = null,

    @JacksonXmlProperty(localName = "link")
    var link: String? = null,

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "description")
    var description: String? = null,

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "category")
    var category: String? = null,

    @JacksonXmlProperty(localName = "ttl")
    var ttl: String? = null,

    @JacksonXmlProperty(localName = "lastBuildDate")
    var lastBuildDate: String? = null,

    @JacksonXmlProperty(localName = "copyright")
    var copyright: String? = null,

    @JacksonXmlProperty(localName = "language")
    var language: String? = null,

    @JacksonXmlProperty(localName = "docs")
    var docs: String? = null,

    @JacksonXmlProperty(localName = "image")
    var image: Image? = null,

    @JacksonXmlElementWrapper(localName = "item", useWrapping = false)
    @JacksonXmlProperty(localName = "item")
    var items: List<Item>? = null
)



@JacksonXmlRootElement(localName = "image")
data class Image(
    @JacksonXmlProperty(localName = "title")
    var title: String? = null,

    @JacksonXmlProperty(localName = "link")
    var link: String? = null,

    @JacksonXmlProperty(localName = "url")
    var url: String? = null
)

@JacksonXmlRootElement(localName = "item")
data class Item(
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "title")
    var title: String? = null,

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "description")
    var description: String? = null,

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "link")
    var link: String? = null,

    @JacksonXmlProperty(localName = "guid")
    var guid: String? = null,

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "category")
    var category: String? = null,

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "pubDate")
    var pubDate: String? = null,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "content", namespace = "http://search.yahoo.com/mrss/")
    var mediaContent: MediaContent? = null
)

@JacksonXmlRootElement(localName = "content", namespace = "http://search.yahoo.com/mrss/")
data class MediaContent(
    @JacksonXmlProperty(localName = "height", isAttribute = true)
    var height: String? = null,

    @JacksonXmlProperty(localName = "medium", isAttribute = true)
    var medium: String? = null,

    @JacksonXmlProperty(localName = "url", isAttribute = true)
    var url: String? = null,

    @JacksonXmlProperty(localName = "width", isAttribute = true)
    var width: String? = null
)