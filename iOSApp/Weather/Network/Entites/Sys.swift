
import Foundation

struct Sys: Codable {

  var type    : Int?    = nil
  var id      : Int?    = nil
  var country : String? = nil
  var sunrise : Int?    = nil
  var sunset  : Int?    = nil

  enum CodingKeys: String, CodingKey {

    case type    = "type"
    case id      = "id"
    case country = "country"
    case sunrise = "sunrise"
    case sunset  = "sunset"
  
  }

  init(from decoder: Decoder) throws {
    let values = try decoder.container(keyedBy: CodingKeys.self)

    type    = try values.decodeIfPresent(Int.self    , forKey: .type    )
    id      = try values.decodeIfPresent(Int.self    , forKey: .id      )
    country = try values.decodeIfPresent(String.self , forKey: .country )
    sunrise = try values.decodeIfPresent(Int.self    , forKey: .sunrise )
    sunset  = try values.decodeIfPresent(Int.self    , forKey: .sunset  )
 
  }

  init() {

  }

}