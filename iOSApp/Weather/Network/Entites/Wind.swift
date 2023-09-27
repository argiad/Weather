
import Foundation

struct Wind: Codable {

  var speed : Double? = nil
  var deg   : Int?    = nil
  var gust  : Double? = nil

  enum CodingKeys: String, CodingKey {

    case speed = "speed"
    case deg   = "deg"
    case gust  = "gust"
  
  }

  init(from decoder: Decoder) throws {
    let values = try decoder.container(keyedBy: CodingKeys.self)

    speed = try values.decodeIfPresent(Double.self , forKey: .speed )
    deg   = try values.decodeIfPresent(Int.self    , forKey: .deg   )
    gust  = try values.decodeIfPresent(Double.self , forKey: .gust  )
 
  }

  init() {

  }

}