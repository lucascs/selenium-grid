module FlickrExample
  
  def run_flickr_scenario(options)
    browser.open "/"
    page.location.should match(%r{http://flickr.com})
    page.type "q", options[:search_string]
    page.click "//form[@action='/search/']//input[@type='submit']", 
               :wait_for => :page
    page.click "link=Advanced Search", :wait_for => :page
    page.click "media_photos"
    page.click "//input[@value='SEARCH']", :wait_for => :page
    page.text?(options[:search_string].split(/ /).first).should be_true
    page.click "//img[@class='pc_img']", :wait_for => :page
    if page.text? "photo_gne_button_zoom"
      page.click "photo_gne_button_zoom"
    end
    page.go_back :wait_for => :page
    page.click "//img[@class='pc_img']", :wait_for => :page
  end
  
end
