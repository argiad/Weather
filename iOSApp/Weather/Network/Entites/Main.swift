
import Foundation

struct Main: Codable {

  var temp      : Double? = nil
  var feelsLike : Double? = nil
  var tempMin   : Double? = nil
  var tempMax   : Double? = nil
  var pressure  : Int?    = nil
  var humidity  : Int?    = nil
  var seaLevel  : Int?    = nil
  var grndLevel : Int?    = nil

  enum CodingKeys: String, CodingKey {

    case temp      = "temp"
    case feelsLike = "feels_like"
    case tempMin   = "temp_min"
    case tempMax   = "temp_max"
    case pressure  = "pressure"
    case humidity  = "humidity"
    case seaLevel  = "sea_level"
    case grndLevel = "grnd_level"
  
  }

  init(from decoder: Decoder) throws {
    let values = try decoder.container(keyedBy: CodingKeys.self)

    temp      = try values.decodeIfPresent(Double.self , forKey: .temp      )
    feelsLike = try values.decodeIfPresent(Double.self , forKey: .feelsLike )
    tempMin   = try values.decodeIfPresent(Double.self , forKey: .tempMin   )
    tempMax   = try values.decodeIfPresent(Double.self , forKey: .tempMax   )
    pressure  = try values.decodeIfPresent(Int.self    , forKey: .pressure  )
    humidity  = try values.decodeIfPresent(Int.self    , forKey: .humidity  )
    seaLevel  = try values.decodeIfPresent(Int.self    , forKey: .seaLevel  )
    grndLevel = try values.decodeIfPresent(Int.self    , forKey: .grndLevel )
 
  }

  init() {

  }

}