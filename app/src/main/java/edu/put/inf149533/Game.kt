package edu.put.inf149533

class Game {
    var id: Long = 0
    var title: String?=null
    var originalTitle: String?=null
    var year: Int=0
    var img: String?=null
    constructor(title:String, originalTitle:String, year: Int, id: Long, img: String){
        this.id=id
        this.title = title
        this.originalTitle=originalTitle
        this.year=year
        this.img=img
    }
}