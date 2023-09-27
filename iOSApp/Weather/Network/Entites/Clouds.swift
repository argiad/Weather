
import Foundation

struct Clouds: Codable {

  var all : Int? = nil

  enum CodingKeys: String, CodingKey {

    case all = "all"
  
  }

  init(from decoder: Decoder) throws {
    let values = try decoder.container(keyedBy: CodingKeys.self)

    all = try values.decodeIfPresent(Int.self , forKey: .all )
 
  }

  init() {

  }

}