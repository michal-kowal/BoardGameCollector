package edu.put.inf149533

class Game {
    var id: Long = 0
    var title: String?=null
    var originalTitle: String?=null
    var year: Int=0
    var img: String?=null
    var thumbnail: String?=null
    constructor(title:String, originalTitle:String, year: Int, id: Long, img: String, thumbnail: String){
        this.id=id
        this.title = title
        this.originalTitle=originalTitle
        this.year=year
        this.img=img
        this.thumbnail=thumbnail
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Game) return false
        return id == other.id
    }
}

class GameDesc{
    var image: String?=null
    var description: String?=null
    var minplayers: Int = 0
    var maxplayers: Int = 0
    var rankValue: String ?=null
    constructor(image:String, description:String, minplayers: Int, maxplayers: Int, rankValue: String){
        this.image=image
        this.description = description
        this.minplayers=minplayers
        this.maxplayers=maxplayers
        this.rankValue=rankValue
    }
}