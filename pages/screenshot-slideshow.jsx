import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";

import React, { useState } from "react";
import { Navigation, Pagination } from "swiper/modules";
import { Swiper, SwiperSlide } from "swiper/react";

const ScreenshotSlideshow = ({ slides }) => {
  const [currentSlide, setCurrentSlide] = useState(0);

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 items-center">
      <div className="p-6 bg-gradient-to-r from-gray-800 via-gray-900 to-black text-white rounded-lg shadow-lg">
        <h3 className="text-2xl font-bold mb-4">
          {slides[currentSlide].title}
        </h3>
        <p className="text-lg">{slides[currentSlide].description}</p>
      </div>

      {/* Swiper Section */}
      <Swiper
        modules={[Navigation, Pagination]}
        navigation
        pagination={{ clickable: true }}
        spaceBetween={20}
        slidesPerView={1}
        onSlideChange={(swiper) => setCurrentSlide(swiper.activeIndex)}
        className="my-4"
      >
        {slides.map((slide, index) => (
          <SwiperSlide key={index}>
            <img
              src={slide.image}
              alt={`Screenshot ${index + 1}`}
              className="w-full rounded-lg shadow-lg"
            />
          </SwiperSlide>
        ))}
      </Swiper>
    </div>
  );
};

export default ScreenshotSlideshow;
